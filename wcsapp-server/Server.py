import calendar
import mwparserfromhell
import urllib2
import urllib
import sys
import xml.etree.ElementTree as ET
from dateutil import parser
from Util import DB
from Util import S3
from Util import Constants
from Util import MLStripper
from Util import DBEntry

def strip_tags(html):
    s = MLStripper()
    s.feed(html)
    return s.get_data()

matches = []
participants = []
schedule = []
DB = DB()
S3 = S3()
        
def handlePage(wikicode, title):
    handleGroup(wikicode.filter(True, r'GroupTableSlot|GroupTableStart'), title)
    handleBracket(wikicode.filter(True, r'WCSChallengerBracket'), 'c', title)
    handleBracket(wikicode.filter(True, r'^ChallengerBracket'), 'c', title)
    handleBracket(wikicode.filter(True, r'8SEBracket'), '8', title)
    handleBracket(wikicode.filter(True, r'4SEBracket'), '4', title)

def handleGroup(wikicode, title):
    for entry in wikicode:
        if type(entry) != mwparserfromhell.nodes.template.Template:
            continue;
        if unicode(entry.name) == "GroupTableStart":
            handleGroupTable(entry, title)
        else:
            handleGroupEntry(entry)

def getDivisionFromTitle(title):
    if ("Challenger" in title) or ("Code A" in title): 
        return "c"
    elif ("Premier" in title) or ("Code S" in title):
        return "p"
    return "x"

def getRegionFromTitle(title):
    if "America" in title: 
        return "a"
    elif "Europe" in title:
        return "e"
    elif "Korea" in title:
        return "k"
    return "x"

def getRoundFromTitle(title):
    if "Ro16" in title:
        return "Ro16"
    elif "Ro24" in title:
        return "Ro24"
    elif "Ro32" in title:
        return "Ro32"
    elif "Ro40" in title:
        return "Ro40"
    elif "Group Stage" in title:
        return "group"
    else:
        return "x"

def getTimestampFromDate(date):
    dt = parser.parse(date, tzinfos=Constants.tzd, fuzzy=True)
    return calendar.timegm(dt.utctimetuple()) * 1000

def handleGroupTable(entry, title):
    handleScheduleEntry(entry, title, getStr(entry,'1'))

def handleScheduleEntry(entry, title, name):
    newSchedule = DBEntry()
    time = getStr(entry, 'date')
    if time == None:
        time = ''
    newSchedule.time = getTimestampFromDate(time)
    newSchedule.name = name
    newSchedule.division = getDivisionFromTitle(title)
    newSchedule.region = getRegionFromTitle(title)
    newSchedule.round = getRoundFromTitle(title)
    schedule.append(newSchedule)
    
def getStr(entry, tag):
    if entry.has(tag):
        return unicode(entry.get(tag).value).strip()
    else:
        return None

def getInt(entry, tag):
    if entry.has(tag):
        return int(unicode(entry.get(tag).value))
    else:
        return None

def handleGroupEntry(entry):
    newParticipant = DBEntry()
    for param in entry.params:
        for template in param.value.filter_templates():
            if unicode(template.name) == 'player':
                newParticipant.name = getStr(template, '1')
                newParticipant.flag = getStr(template, 'flag')
                newParticipant.race = getStr(template, 'race')
    newParticipant.place = getInt(entry, 'place')
    newParticipant.matcheswon = getInt(entry, 'win_m')
    newParticipant.matcheslost = getInt(entry, 'lose_m')
    newParticipant.mapswon = getInt(entry, 'win_g')
    newParticipant.mapslost = getInt(entry, 'lose_g')
    newParticipant.result = getStr(entry, 'bg')
    newParticipant.scheduleid = len(schedule)
    participants.append(newParticipant)
        
def handleBracket(wikicode, bracketType, title):
    if bracketType == 'c':
        bracketSize = 4
    else:
        bracketSize = int(bracketType) - 1
    for entry in wikicode:
        if type(entry) != mwparserfromhell.nodes.template.Template:
            continue;
        for matchNum in range(bracketSize):
            handleBracketEntry(entry, 
                               Constants.prefixes[bracketType][matchNum][0], 
                               Constants.prefixes[bracketType][matchNum][1], 
                               Constants.prefixes[bracketType][matchNum][2],
                               title)
    
def handleBracketEntry(entry, prefix1, prefix2, prefixg, title):
    # This somewhat ugly block of code will be replaced when game parsing is implemented
    if entry.has(prefixg + 'details'):
        handleScheduleEntry(entry.get(prefixg + 'details').value.filter_templates(matches=r'BracketMatchSummary')[0],
                            title, 
                            unicode(entry.get(prefix1[:2]).value).strip() if entry.has(prefix1[:2]) else unicode(entry.name).strip())
    newMatch = DBEntry()
    newMatch.matchtype = 'bracket'
    newMatch.scheduleid = len(schedule)
    newMatch.player1name = getStr(entry, prefix1)
    newMatch.player2name = getStr(entry, prefix2)
    newMatch.player1race = getStr(entry, prefix1 + 'race')
    newMatch.player2race = getStr(entry, prefix2 + 'race')
    newMatch.player1flag = getStr(entry, prefix1 + 'flag')
    newMatch.player2flag = getStr(entry, prefix2 + 'flag')
    newMatch.player1wins = getStr(entry, prefix1 + 'score')
    newMatch.player2wins = getStr(entry, prefix2 + 'score')
    if entry.has(prefix1 + 'win') and getInt(entry, prefix1 + 'win') > 0:
        newMatch.winner = 1
    elif entry.has(prefix2 + 'win') and getInt(entry, prefix2 + 'win') > 0:
        newMatch.winner = 2
    else:
        newMatch.winner = None
    matches.append(newMatch)

url_str = 'http://wiki.teamliquid.net/starcraft2/api.php?action=query&export&exportnowrap&titles=' + ('|'.join(map(urllib.quote_plus, Constants.pageNames)))
try:
    url = urllib2.urlopen(url_str)
    MW_XML_PREFIX = "{http://www.mediawiki.org/xml/export-0.8/}"
    root_xml = ET.fromstring(url.read())
except (urllib2.URLError, httplib.IncompleteRead):
    sys.exit()

for page_xml in root_xml.iter(MW_XML_PREFIX + "page"):
    wikicode = mwparserfromhell.parse(strip_tags(page_xml.find(MW_XML_PREFIX + "revision").find(MW_XML_PREFIX + "text").text))
    handlePage(wikicode, page_xml[0].text)
    
DB.initDB()
DB.insert(matches, "matches")
DB.insert(participants, "participants")
DB.insert(schedule, "schedule")
S3.uploadData(DB)

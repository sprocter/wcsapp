import calendar
import mwparserfromhell
import urllib2
import urllib
import sys
import xml.etree.ElementTree as ET
from mwparserfromhell.nodes import (Template, Tag, Text)
from dateutil import parser
from Util import (DB, S3, Constants, MLStripper, DBEntry)

def strip_tags(html):
    s = MLStripper()
    s.feed(html)
    return s.get_data()

lastSchedule = DBEntry()
matches = []
participants = []
games = []
standings = []
scheduleId = 1
scheduleMap = Constants.Schedule
schedule = []
DB = DB()
S3 = S3()
        
def handlePage(wikicode, title):
    if 'Standings' in title:
        handleStandings(wikicode)
        return
    if 'GroupTableSlot' in unicode(wikicode) and 'MatchList' in unicode(wikicode):
        handleGroup(wikicode.filter(True, r'GroupTableSlot|GroupTableStart|MatchList', forcetype=Template), title)
    else:
        handleHeadsUpMatchList(wikicode.filter(True, r'MatchList', forcetype=Template), title)
    handleBracket(wikicode.filter(True, r'16SEBracket', forcetype=Template), '16', title)
    handleBracket(wikicode.filter(True, r'8SEBracket', forcetype=Template), '8', title)
    handleBracket(wikicode.filter(True, r'4SEBracket', forcetype=Template), '4', title)
    handleBracket(wikicode.filter(True, r'2SEBracket', forcetype=Template), '2', title)

def handleStandings(wikicode):
    ranks = wikicode.filter(True, r'\|\d+', forcetype=Text)
    linkifiedNames = wikicode.filter_wikilinks()
    flagRace = wikicode.filter(True, r'flagRace', forcetype=Template)
    points = wikicode.filter(True, r"'''", forcetype=Tag)
    for i in range(0, len(flagRace)):
        standing = DBEntry()
        standing.rank = int((ranks[i][1 + ranks[i].rfind('\n'):].replace('|','')).strip())
        standing.name = unicode(linkifiedNames[i + 2].title)
        standing.flag = unicode(flagRace[i].params[0].value)
        standing.race = unicode(flagRace[i].params[1].value)
        standing.points = int(unicode(points[i].contents))
        standings.append(standing)
        i += 1

def handleHeadsUpMatchList(wikicode, title):
    for entry in wikicode:
        for param in entry.params:
            if param[:3] == 'Day':
                name = unicode(param).strip()
            elif param[:5] == 'match':
                handleScheduleEntry(param.value.get(0), title, name)
                handleHeadsUpMatch(param.value.get(0))

def handleHeadsUpMatch(match):
    newMatch = DBEntry()
    newMatch.matchtype = 'bracket'
    newMatch.scheduleid = lastSchedule.id
    newMatch.player1name = getStr(match, 'player1')
    newMatch.player2name = getStr(match, 'player2')
    newMatch.player1race = getStr(match, 'player1race')
    newMatch.player2race = getStr(match, 'player2race')
    newMatch.player1flag = getStr(match, 'player1flag')
    newMatch.player2flag = getStr(match, 'player2flag')
    newMatch.player1wins, newMatch.player2wins = handleGame(match)
    if match.has('winner') and getInt(match, 'winner') > 0:
        newMatch.winner = getInt(match, 'winner')
    else:
        newMatch.winner = None
    matches.append(newMatch)

def handleGroup(wikicode, title):
    for entry in wikicode:
        if unicode(entry.name) == "GroupTableStart":
            handleGroupTable(entry, title)
        elif unicode(entry.name) == "GroupTableSlot":
            handleGroupEntry(entry)
        elif unicode(entry.name.strip()) == "MatchList":
            handleMatchList(entry)
        else:
            print "Got garbage while handling group: " + entry.name

def handleMatchList(entry):
    try:
        for param in entry.params:
            if param.name == "title":
                matchTitle = param.value
            elif param.name[:5] == "match": 
                handleMatch(param.value.filter(True, r'MatchMaps')[0], matchTitle)
    except NameError:
        handleMatch(param.value.filter(True, r'MatchMaps')[0], "Match List")

def handleMatch(match, matchTitle):
    newMatch = DBEntry()
    newMatch.matchtype = "group"
    newMatch.scheduleid = lastSchedule.id
    newMatch.player1name = getStr(match, "player1")
    newMatch.player2name = getStr(match, "player2")
    newMatch.player1race = getStr(match, "player1race")
    newMatch.player2race = getStr(match, "player2race")
    newMatch.player1flag = "-"
    newMatch.player2flag = "-"
    p1wins, p2wins = handleGame(match)
    newMatch.player1wins = p1wins
    newMatch.player2wins = p2wins
    newMatch.winner = getInt(match, "winner")
    matches.append(newMatch)

def handleGame(match):
    matchid = len(matches) + 1
    i = 1
    p1wins = 0
    p2wins = 0
    while match.has("map" + str(i) + "win", False):
        newGame = DBEntry()
        newGame.mapname = getStr(match, "map" + str(i))
        mapwinner = getInt(match, "map" + str(i) + "win")
        newGame.mapwinner = mapwinner
        if mapwinner == 1:
            p1wins = p1wins + 1
        elif mapwinner == 2:
            p2wins = p2wins + 1
        newGame.matchid = matchid
        newGame.vodlink = getStr(match, "vodgame" + str(i))
        i = i + 1
        games.append(newGame)
    return p1wins, p2wins

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
    elif "Korea" in title or "Global StarCraft II League" in title:
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
    else:
        return "group"

def getTimestampFromDate(date):
    try:
        dt = parser.parse(date, tzinfos=Constants.tzd, fuzzy=True)
    except ValueError:
        # Got a garbage date, use the closest thing we have...
        return int(schedule[len(schedule) - 1].time) + 1
    return calendar.timegm(dt.utctimetuple()) * 1000

def handleGroupTable(entry, title):
    handleScheduleEntry(entry, title, getStr(entry,'1'))

def handleScheduleEntry(entry, title, name):
    global scheduleId
    global lastSchedule
    lastSchedule = DBEntry()
    time = getStr(entry, 'date')
    if time == None:
        time = ''
    lastSchedule.time = getTimestampFromDate(time)
    lastSchedule.name = name
    lastSchedule.division = getDivisionFromTitle(title)
    lastSchedule.region = getRegionFromTitle(title)
    lastSchedule.round = getRoundFromTitle(title)
    lastSchedule.id = scheduleId;
    try:
        mergedSchedule = False
        for candidateSchedule in scheduleMap[lastSchedule.region][lastSchedule.division][lastSchedule.round]:
            if ((candidateSchedule.time < lastSchedule.time + 2 * 60 * 60 * 1000) and
                (candidateSchedule.time > lastSchedule.time - 6 * 60 * 60 * 1000) and
                candidateSchedule.name == lastSchedule.name):
                lastSchedule = candidateSchedule
                mergedSchedule = True
        if not mergedSchedule:
            scheduleId += 1
            scheduleMap[lastSchedule.region][lastSchedule.division][lastSchedule.round].append(lastSchedule)
            schedule.append(lastSchedule)
    except KeyError:
        print "KeyError for " + lastSchedule.region + " - " + lastSchedule.division + " - " + lastSchedule.round
    
def getStr(entry, tag):
    if entry.has(tag):
        return unicode(entry.get(tag).value).strip()
    else:
        return None

def getInt(entry, tag):
    try:
        if entry.has(tag):
            return int(unicode(entry.get(tag).value))
        else:
            return None
    except ValueError:
        return None # Someone typed something in that wasn't a number

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
    newParticipant.scheduleid = lastSchedule.id
    participants.append(newParticipant)
        
def handleBracket(wikicode, bracketType, title):
    if bracketType == 'c':
        bracketSize = 4
    else:
        bracketSize = int(bracketType) - 1
    for entry in wikicode:
        for matchNum in range(bracketSize):
            handleBracketEntry(entry, 
                               Constants.prefixes[bracketType][matchNum][0], 
                               Constants.prefixes[bracketType][matchNum][1], 
                               Constants.prefixes[bracketType][matchNum][2],
                               title)
    
def handleBracketEntry(entry, prefix1, prefix2, prefixg, title):
    if entry.has(prefixg + 'details'):
        bracketMatchSummary = entry.get(prefixg + 'details').value.filter_templates(matches=r'BracketMatchSummary')[0]
        handleScheduleEntry(bracketMatchSummary, title, 
                            unicode(entry.get(prefix1[:2]).value).strip() if entry.has(prefix1[:2]) else unicode(entry.name).strip())
        handleGame(bracketMatchSummary)
    newMatch = DBEntry()
    newMatch.matchtype = 'bracket'
    newMatch.scheduleid = lastSchedule.id
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
DB.insert(standings, "standings")
DB.insert(matches, "matches")
DB.insert(participants, "groups")
DB.insert(schedule, "schedule")
DB.insert(games, "maps")
S3.uploadData(DB)

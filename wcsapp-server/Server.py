import calendar
import mwparserfromhell
import urllib2
import urllib
import xml.etree.ElementTree as ET
from dateutil import parser
from HTMLParser import HTMLParser
from Util import DB
from Util import S3

"""
This... may be incorrect.  Basically every time an object needs to be put
into the database, this class is instantiated and then fields are added. As
long as those fields' names exactly match the column names in the database,
then the DB.insert(...) method in Util.py will make it all work.
"""
class DBEntry:
    pass

"""
I straight up do not understand this class, or the strip_tags function below
it, which is not a good thing.  At some point I should probably have someone
explain it to me, or step through it with a debugger or something.  It comes
from Peter Mortensen at http://stackoverflow.com/a/925630
"""
class MLStripper(HTMLParser):
    def __init__(self):
        self.reset()
        self.fed = []
    def handle_data(self, d):
        self.fed.append(d)
    def get_data(self):
        return ''.join(self.fed)

def strip_tags(html):
    s = MLStripper()
    s.feed(html)
    return s.get_data()

matches = []
participants = []
schedule = []
DB = DB()
S3 = S3()

"""
I struggled for a while on how to represent the prefixes for match-specific
player ids in a way that wasn't super redundant.  Hard-coding maps isn't often
correct, but I think it fits here.
"""
prefixes = {}
prefixes['c'] = []
prefixes['c'].append(('R1D1', 'R1D2', 'R1G1'))
prefixes['c'].append(('R1D3', 'R1D4', 'R1G2'))
prefixes['c'].append(('R2W1', 'R2W2', 'R2G1'))
prefixes['c'].append(('R3D1', 'R3W1', 'R3G1'))
prefixes['4'] = []
prefixes['4'].append(('R1D1', 'R1D2', 'R1G1'))
prefixes['4'].append(('R1D3', 'R1D4', 'R1G2'))
prefixes['4'].append(('R2W1', 'R2W2', 'R2G1'))
prefixes['8'] = []
prefixes['8'].append(('R1D1', 'R1D2', 'R1G1'))
prefixes['8'].append(('R1D3', 'R1D4', 'R1G2'))
prefixes['8'].append(('R1D5', 'R1D6', 'R1G3'))
prefixes['8'].append(('R1D7', 'R1D8', 'R1G4'))
prefixes['8'].append(('R2W1', 'R2W2', 'R2G1'))
prefixes['8'].append(('R2W3', 'R2W4', 'R2G2'))
prefixes['8'].append(('R3W1', 'R3W2', 'R3G1'))

pageNames = ['2013 WCS Season 3',
             '2013 WCS Season 3 America/Premier/Ro32',
             '2013 WCS Season 3 America/Premier/Ro16',
             '2013 WCS Season 3 America/Premier',
             '2013 WCS Season 3 Europe/Premier/Ro32',
             '2013 WCS Season 3 Europe/Premier/Ro16',
             '2013 WCS Season 3 Europe/Premier',
             '2013 WCS Season 3 Korea GSL/Premier/Ro32',
             '2013 WCS Season 3 Korea GSL/Premier/Ro16',
             '2013 WCS Season 3 Korea GSL/Premier',
             '2013 WCS Season 3 America/Premier',
             '2013 WCS Season 3 America/Challenger',
             '2013 WCS Season 3 Europe/Challenger',
             '2013 WCS Season 3 Korea GSL/Challenger',
             '2013 WCS Season 3 Korea GSL/Up and Down Matches'
             '2013 WCS Season 3 Europe/Challenger/Group Stage',
             '2013 WCS Season 3 America/Challenger/Group Stage']

"""
Holy crap, dealing with time zones / parsing time is a huge pain compared to,
say, PHP.  Here's an only sort of hackish solution from Nas Banov, at
http://stackoverflow.com/a/4766400
"""
tz_str = '''-12 Y
-11 X NUT SST
-10 W CKT HAST HST TAHT TKT
-9 V AKST GAMT GIT HADT HNY
-8 U AKDT CIST HAY HNP PST PT
-7 T HAP HNR MST PDT
-6 S CST EAST GALT HAR HNC MDT
-5 R CDT COT EASST ECT EST ET HAC HNE PET
-4 Q AST BOT CLT COST EDT FKT GYT HAE HNA PYT
-3 P ADT ART BRT CLST FKST GFT HAA PMST PYST SRT UYT WGT
-2 O BRST FNT PMDT UYST WGST
-1 N AZOT CVT EGT
0 Z EGST GMT UTC WET WT
1 A CET DFT WAT WEDT WEST
2 B CAT CEDT CEST EET SAST WAST
3 C EAT EEDT EEST IDT MSK
4 D AMT AZT GET GST KUYT MSD MUT RET SAMT SCT
5 E AMST AQTT AZST HMT MAWT MVT PKT TFT TJT TMT UZT YEKT
6 F ALMT BIOT BTT IOT KGT NOVT OMST YEKST
7 G CXT DAVT HOVT ICT KRAT NOVST OMSST THA WIB
8 H ACT AWST BDT BNT CAST HKT IRKT KRAST MYT PHT SGT ULAT WITA WST
9 I AWDT IRKST JST KST PWT TLT WDT WIT YAKT
10 K AEST ChST PGT VLAT YAKST YAPT
11 L AEDT LHDT MAGT NCT PONT SBT VLAST VUT
12 M ANAST ANAT FJT GILT MAGST MHT NZST PETST PETT TVT WFT
13 FJST NZDT
11.5 NFT
10.5 ACDT LHST
9.5 ACST
6.5 CCT MMT
5.75 NPT
5.5 SLT
4.5 AFT IRDT
3.5 IRST
-2.5 HAT NDT
-3.5 HNT NST NT
-4.5 HLV VET
-9.5 MART MIT'''

tzd = {}
for tz_descr in map(str.split, tz_str.split('\n')):
    tz_offset = int(float(tz_descr[0]) * 3600)
    for tz_code in tz_descr[1:]:
        tzd[tz_code] = tz_offset
        
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
    dt = parser.parse(date, tzinfos=tzd, fuzzy=True)
    return calendar.timegm(dt.utctimetuple()) * 1000

def handleGroupTable(entry, title):
    handleScheduleEntry(entry, title, unicode(entry.get('1')).strip())

def handleScheduleEntry(entry, title, name):
    newSchedule = DBEntry()
    newSchedule.time = getTimestampFromDate(unicode(entry.get('date').value))
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
            handleBracketEntry(entry, prefixes[bracketType][matchNum][0], prefixes[bracketType][matchNum][1], prefixes[bracketType][matchNum][2], title)
    
def handleBracketEntry(entry, prefix1, prefix2, prefixg, title):
    # This somewhat ugly block of code will be replaced when game parsing is implemented
    if entry.has(prefixg + 'details'):
        handleScheduleEntry(entry.get(prefixg + 'details').value.filter_templates(matches=r'BracketMatchSummary')[0],
                            title, unicode(entry.get(prefix1[:2]).value).strip() if entry.has(prefix1[:2]) else unicode(entry.name).strip())
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

url_str = 'http://wiki.teamliquid.net/starcraft2/api.php?action=query&export&exportnowrap&titles=' + ('|'.join(map(urllib.quote_plus, pageNames)))
url = urllib2.urlopen(url_str)
MW_XML_PREFIX = "{http://www.mediawiki.org/xml/export-0.8/}"
root_xml = ET.fromstring(url.read())

for page_xml in root_xml.iter(MW_XML_PREFIX + "page"):
    wikicode = mwparserfromhell.parse(strip_tags(page_xml.find(MW_XML_PREFIX + "revision").find(MW_XML_PREFIX + "text").text))
    handlePage(wikicode, page_xml[0].text)
    
DB.initDB()
DB.insert(matches, "matches")
DB.insert(participants, "participants")
DB.insert(schedule, "schedule")
S3.uploadData(DB)

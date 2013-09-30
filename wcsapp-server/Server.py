import mwparserfromhell
import xml.etree.ElementTree as ET
import urllib2
import urllib
from HTMLParser import HTMLParser
from Util import DB
from Util import S3


matches = []
participants = []

"""
I struggled for a while on how to represent the prefixes for match-specific
player ids in a way that wasn't super redundant.  Hard-coding maps isn't often
correct, but I think it fits here.
"""
prefixes = {}
prefixes['c'] = []
prefixes['c'].append(('R1D1', 'R1D2'))
prefixes['c'].append(('R1D3', 'R1D4'))
prefixes['c'].append(('R2W1', 'R2W2'))
prefixes['c'].append(('R3D1', 'R3W1'))
prefixes['4'] = []
prefixes['4'].append(('R1D1', 'R1D2'))
prefixes['4'].append(('R1D3', 'R1D4'))
prefixes['4'].append(('R2W1', 'R2W2'))
prefixes['8'] = []
prefixes['8'].append(('R1D1', 'R1D2'))
prefixes['8'].append(('R1D3', 'R1D4'))
prefixes['8'].append(('R1D5', 'R1D6'))
prefixes['8'].append(('R1D7', 'R1D8'))
prefixes['8'].append(('R2W1', 'R2W2'))
prefixes['8'].append(('R2W3', 'R2W4'))
prefixes['8'].append(('R3W1', 'R3W2'))

class DBEntry:
    pass

"""
I straight up do not understand this class, or the strip_tags function below
it, which is not a good thing.  At some point I should probably have someone
explain it to me, or step through it with a debugger or something.  It comes
from Peter Mortensen at http://stackoverflow.com/questions/753052
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

def handlePage(wikicode):
    handleGroup(wikicode.filter(True, r'GroupTableSlot|GroupTableStart'))
    handleBracket(wikicode.filter(True, r'WCSChallengerBracket'), 'c')
    handleBracket(wikicode.filter(True, r'8SEBracket'), '8')
    handleBracket(wikicode.filter(True, r'4SEBracket'), '4')

def handleGroup(wikicode):
    for entry in wikicode:
        if type(entry) != mwparserfromhell.nodes.template.Template:
            continue;
        if unicode(entry.name) == "GroupTableStart":
            pass
        else:
            handleGroupEntry(entry)

def handleGroupEntry(entry):
    newParticipant = DBEntry()
    for param in entry.params:
        for template in param.value.filter_templates():
            if unicode(template.name) == 'player':
                if unicode(template.params[2].name) == '1':
                    newParticipant.name = unicode(template.params[2].value)
                newParticipant.flag = unicode(template.get('flag').value)
                newParticipant.race = unicode(template.get('race').value)
    newParticipant.place = int(unicode(entry.get('place').value))
    newParticipant.matcheswon = int(unicode(entry.get('win_m').value))
    newParticipant.matcheslost = int(unicode(entry.get('lose_m').value))
    newParticipant.mapswon = int(unicode(entry.get('win_g').value))
    newParticipant.mapslost = int(unicode(entry.get('lose_g').value))
    newParticipant.result = unicode(entry.get('bg').value)
    participants.append(newParticipant)
        
def handleBracket(wikicode, bracketType):
    if bracketType == 'c':
        bracketSize = 4
    else:
        bracketSize = int(bracketType) - 1
    for entry in wikicode:
        if type(entry) != mwparserfromhell.nodes.template.Template:
            continue;
        for matchNum in range(bracketSize):
            handleBracketEntry(entry, prefixes[bracketType][matchNum][0], prefixes[bracketType][matchNum][1])
    
def handleBracketEntry(entry, prefix1, prefix2):
    newMatch = DBEntry()
    newMatch.matchtype = 'bracket'
    newMatch.scheduleid = 14
    newMatch.player1name = unicode(entry.get(prefix1).value)
    newMatch.player2name = unicode(entry.get(prefix2).value)
    newMatch.player1race = unicode(entry.get(prefix1 + 'race').value)
    newMatch.player2race = unicode(entry.get(prefix2 + 'race').value)
    newMatch.player1flag = unicode(entry.get(prefix1 + 'flag').value)
    newMatch.player2flag = unicode(entry.get(prefix2 + 'flag').value)
    newMatch.player1wins = unicode(entry.get(prefix1 + 'score').value)
    newMatch.player2wins = unicode(entry.get(prefix2 + 'score').value)
    if entry.has(prefix1 + 'win') and int(unicode(entry.get(prefix1 + 'win').value)) > 0:
        newMatch.winner = 1
    elif entry.has(prefix2 + 'win') and int(unicode(entry.get(prefix2 + 'win').value)) > 0:
        newMatch.winner = 2
    matches.append(newMatch)

pageNames = ['2013 WCS Season 1 America/Premier/Ro32',
    '2013 WCS Season 1 America/Premier/Ro16',
    '2013 WCS Season 1 America/Premier',
    '2013 WCS Season 1 Europe/Premier/Ro32',
    '2013 WCS Season 1 Europe/Premier/Ro16',
    '2013 WCS Season 1 Europe/Premier',
    '2013 WCS Season 1 Korea GSL/Code S/Ro32',
    '2013 WCS Season 1 Korea GSL/Code S/Ro16',
    '2013 WCS Season 1 Korea GSL/Code S',
    '2013 WCS Season 1 America/Premier',
    '2013 WCS Season 1 America/Challenger',
    '2013 WCS Season 1 Europe/Challenger',
    '2013 WCS Season 1 Korea GSL/Challenger',
    '2013 WCS Season 1 Europe/Challenger/Group Stage',
    '2013 WCS Season 1 America/Challenger/Group Stage']
url_str = 'http://wiki.teamliquid.net/starcraft2/api.php?action=query&export&exportnowrap&titles=' + ('|'.join(map(urllib.quote_plus, pageNames)))
url = urllib2.urlopen(url_str)
MW_XML_PREFIX = "{http://www.mediawiki.org/xml/export-0.8/}"
root_xml = ET.fromstring(url.read())

for page_xml in root_xml.iter(MW_XML_PREFIX + "page"):
    wikicode = mwparserfromhell.parse(strip_tags(page_xml.find(MW_XML_PREFIX + "revision").find(MW_XML_PREFIX + "text").text))
    handlePage(wikicode)
    
DB = DB()
DB.initDB()
DB.insert(matches, "matches")
DB.insert(participants, "participants")
S3 = S3()
S3.uploadData(DB)
DB.close()

"""
Version checking stuff...

url_str = 'http://wiki.teamliquid.net/starcraft2/api.php?action=query&prop=revisions&rvprop=ids&format=xml&titles=' + ('|'.join(map(urllib.quote_plus, pageNames)))
url = urllib2.urlopen(url_str)
root_xml = ET.fromstring(url.read())

for page_xml in root_xml.iter("page"):
    pageNames.append(page_xml.get("title"))
    page_xml.find("revisions").find("rev").get("revid")
"""
#print wikicode
import sqlite3
import gzip
import StringIO
import boto
from HTMLParser import HTMLParser

class DB:
    conn = None
    
    def initDB(self):
        self.conn = sqlite3.connect('wcsapp.sqlite')
        cursor = self.conn.cursor()
        cursor.execute('DROP TABLE IF EXISTS maps')
        cursor.execute('DROP TABLE IF EXISTS matches')
        cursor.execute('DROP TABLE IF EXISTS schedule')
        cursor.execute('DROP TABLE IF EXISTS groups')
        cursor.execute('DROP TABLE IF EXISTS standings')
        cursor.execute('CREATE TABLE "maps" ("id" INTEGER PRIMARY KEY  NOT NULL ,"mapname" TEXT,"mapwinner" INTEGER DEFAULT (null) ,"vodlink" TEXT,"matchid" INTEGER NOT NULL  DEFAULT (null) )')
        cursor.execute('CREATE TABLE "matches" ("id" INTEGER PRIMARY KEY  NOT NULL ,"winner" TEXT,"player1name" TEXT,"player2name" TEXT,"player1race" TEXT,"player2race" TEXT,"player1flag" TEXT,"player2flag" TEXT,"numgames" INTEGER DEFAULT (null) ,"matchname" TEXT,"scheduleid" INTEGER NOT NULL  DEFAULT (null) , "matchnum" INTEGER, "matchtype" TEXT, "player1wins" TEXT, "player2wins" TEXT)')
        cursor.execute('CREATE TABLE "schedule" ("id" INTEGER PRIMARY KEY NOT NULL ,"time" INTEGER,"division" TEXT,"region" TEXT,"name" TEXT, "round" TEXT)')
        cursor.execute('CREATE TABLE "groups" ("id" INTEGER PRIMARY KEY NOT NULL, "name" TEXT, "flag" TEXT, "race" TEXT, "place" INTEGER, "matcheswon" INTEGER, "matcheslost" INTEGER, "mapswon" INTEGER, "mapslost" INTEGER, "result" TEXT, "scheduleid" INTEGER)')
        cursor.execute('CREATE TABLE "standings" ("id" INTEGER PRIMARY KEY NOT NULL, "rank" INTEGER, "name" TEXT, "flag" TEXT, "race" TEXT, "points" INTEGER)')
        self.conn.commit()
        
    def insert(self, objs, tableName):
        cursor = self.conn.cursor()
        colnames = vars(objs[0]).keys()
        qmarks = ['?' for x in range(len(colnames))]
        query = 'INSERT INTO ' + tableName + ' ("' + '","'.join(colnames) + '") VALUES (' + ', '.join(qmarks) + ')' 
        cursor.executemany(query, (vars(match).values() for match in objs))
        self.conn.commit()

    def getDB(self):
        out = StringIO.StringIO()
        with gzip.GzipFile(fileobj=out, mode='wb') as f:
            for line in self.conn.iterdump():
                f.write('%s\n' % line)
        self.close()
        return out.getvalue()
        """self.close()
        out = StringIO.StringIO()
        f_in = open('wcsapp.sqlite', 'rb')
        f_out = gzip.GzipFile(fileobj=out, mode='wb')
        f_out.writelines(f_in)
        f_out.close()
        f_in.close()
        return out.getvalue()"""
    
    def close(self):
        self.conn.close()
        
class S3:
    conn = None
    
    def initS3Conn(self):
        keyfile = open('s3keys.txt')
        access_key = keyfile.readline().strip()
        secret_key = keyfile.readline().strip()
        keyfile.close()
        
        self.conn = boto.connect_s3(
            aws_access_key_id = access_key,
            aws_secret_access_key = secret_key,
            host = 'objects.dreamhost.com')
    
    def uploadData(self, db):
        self.initS3Conn()
        bucket = self.conn.create_bucket('sc2wcsapp')
        key = bucket.get_key('data/sqlite.db.gz')
        key.set_contents_from_string(db.getDB())
        key.set_acl('public-read')
        #print key.generate_url(0, query_auth=False, force_http=True)

class Constants:
    """
    I struggled for a while on how to represent the prefixes for match-specific
    player ids in a way that wasn't super redundant.  Hard-coding maps isn't often
    correct, but I think it fits here.
    """
    prefixes = {}
    prefixes['2'] = []
    prefixes['2'].append(('R1D1', 'R1D2', 'R1G1'))
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
    prefixes['16'] = []
    prefixes['16'].append(('R1D1', 'R1D2', 'R1G1'))
    prefixes['16'].append(('R1D3', 'R1D4', 'R1G2'))
    prefixes['16'].append(('R1D5', 'R1D6', 'R1G3'))
    prefixes['16'].append(('R1D7', 'R1D8', 'R1G4'))
    prefixes['16'].append(('R1D9', 'R1D10', 'R1G5'))
    prefixes['16'].append(('R1D11', 'R1D12', 'R1G6'))
    prefixes['16'].append(('R1D13', 'R1D14', 'R1G7'))
    prefixes['16'].append(('R1D15', 'R1D16', 'R1G8'))
    prefixes['16'].append(('R2W1', 'R2W2', 'R2G1'))
    prefixes['16'].append(('R2W3', 'R2W4', 'R2G2'))
    prefixes['16'].append(('R2W5', 'R2W6', 'R2G3'))
    prefixes['16'].append(('R2W7', 'R2W8', 'R2G4'))
    prefixes['16'].append(('R3W1', 'R3W2', 'R3G1'))
    prefixes['16'].append(('R3W3', 'R3W4', 'R3G2'))
    prefixes['16'].append(('R4W1', 'R4W2', 'R4G1'))
    
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

    pageNames = ['2014 Global StarCraft II League Season 1/Code A',
                 '2014 Global StarCraft II League Season 1/Code S/Ro32',
                 '2014 WCS Season 1 Europe/Challenger',
                 '2014 WCS Season 1 America/Challenger',
                 '2014 StarCraft II World Championship Series/Standings']
    
    Schedule = {}
    Schedule['k'] = {}
    Schedule['k']['c'] = {}
    Schedule['k']['c']['Ro40'] = []
    Schedule['k']['c']['Ro24'] = []
    Schedule['k']['c']['group'] = []
    Schedule['k']['p'] = {}
    Schedule['k']['p']['Ro32'] = []
    Schedule['k']['p']['Ro16'] = []
    Schedule['k']['p']['group'] = []
    Schedule['e'] = {}
    Schedule['e']['c'] = {}
    Schedule['e']['c']['Ro40'] = []
    Schedule['e']['c']['Ro24'] = []
    Schedule['e']['c']['group'] = []
    Schedule['a'] = {}
    Schedule['a']['c'] = {}
    Schedule['a']['c']['Ro40'] = []
    Schedule['a']['c']['Ro24'] = []
    Schedule['a']['c']['group'] = []
'''    Schedule['a'] = {}
    Schedule['a']['c'] = {}
    Schedule['a']['c']['Ro40'] = []
    Schedule['a']['c']['Ro24'] = []
    Schedule['a']['c']['group'] = []
    Schedule['a']['p'] = {}
    Schedule['a']['p']['Ro32'] = []
    Schedule['a']['p']['Ro16'] = []
    Schedule['a']['p']['group'] = []
    Schedule['a']['x'] = {} 
    Schedule['a']['x']['x'] = []
    Schedule['e']['p'] = {}
    Schedule['e']['p']['Ro32'] = []
    Schedule['e']['p']['Ro16'] = []
    Schedule['e']['p']['group'] = []
    Schedule['e']['x'] = {}
    Schedule['e']['x']['x'] = []
    Schedule['k']['p'] = {}
    Schedule['k']['p']['Ro32'] = []
    Schedule['k']['p']['Ro16'] = []
    Schedule['k']['p']['group'] = []
    Schedule['k']['x'] = {}
    Schedule['k']['x']['x'] = []
    Schedule['x'] = {}
    Schedule['x']['x'] = {}
    Schedule['x']['x']['group'] = []'''
    
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
    
"""
This... may be incorrect.  Basically every time an object needs to be put
into the database, this class is instantiated and then fields are added. As
long as those fields' names exactly match the column names in the database,
then the DB.insert(...) method in Util.py will make it all work.
"""
class DBEntry:
    pass
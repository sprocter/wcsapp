import sqlite3
import gzip
import StringIO
import boto

class DB:
    conn = None
    
    def initDB(self):
        self.conn = sqlite3.connect('wcsapp.sqlite')
        cursor = self.conn.cursor()
        cursor.execute('DROP TABLE IF EXISTS games')
        cursor.execute('DROP TABLE IF EXISTS matches')
        cursor.execute('DROP TABLE IF EXISTS schedule')
        cursor.execute('DROP TABLE IF EXISTS participants')
        cursor.execute('CREATE TABLE "games" ("id" INTEGER PRIMARY KEY  NOT NULL ,"mapname" TEXT,"mapwinner" INTEGER DEFAULT (null) ,"vodlink" TEXT,"matchid" INTEGER NOT NULL  DEFAULT (null) )')
        cursor.execute('CREATE TABLE "matches" ("id" INTEGER PRIMARY KEY  NOT NULL ,"winner" TEXT,"player1name" TEXT,"player2name" TEXT,"player1race" TEXT,"player2race" TEXT,"player1flag" TEXT,"player2flag" TEXT,"numgames" INTEGER DEFAULT (null) ,"matchname" TEXT,"scheduleid" INTEGER NOT NULL  DEFAULT (null) , "matchnum" INTEGER, "matchtype" TEXT, "player1wins" TEXT, "player2wins" TEXT)')
        cursor.execute('CREATE TABLE "schedule" ("id" INTEGER PRIMARY KEY NOT NULL ,"time" INTEGER,"division" TEXT,"region" TEXT,"name" TEXT, "round" TEXT)')
        cursor.execute('CREATE TABLE "participants" ("id" INTEGER PRIMARY KEY NOT NULL, "name" TEXT, "flag" TEXT, "race" TEXT, "place" INTEGER, "matcheswon" INTEGER, "matcheslost" INTEGER, "mapswon" INTEGER, "mapslost" INTEGER, "result" TEXT, "scheduleid" INTEGER)')
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
        print key.generate_url(0, query_auth=False, force_http=True)
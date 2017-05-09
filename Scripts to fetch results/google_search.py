#!/bin/python
import os
import bs4
from bs4 import BeautifulSoup
import urllib.request as urllib2
from google import search
import requests
from pymongo import MongoClient
import re
import sys
import time

client = MongoClient()
db = client.test

def containsNonAscii(s):
    return any(ord(i)>127 for i in s)

def containsNonAscii(s):
    return any(ord(i)>127 for i in s)

def google_search(query):
    j = 0
    for url in search(query, stop=40):
        if j==31:
            print("Terminating as got 30 results"+str(j))
            return
        else:
            print("not yet 30")
        #out =open('file['+str(j)+'].txt','w')
        #print(url)
        if 'stackoverflow.com' in url:
            #print("ignoring stackoverflow.com")
            continue

        j = j+1
        #if(j==15):
#            time.sleep(120)
        #print(j)
        #if j==3:
          #break
        hdr = {'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11',
       'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'}
        try:
            req = urllib2.Request(url,headers=hdr)
            res = urllib2.urlopen(req).read()
            soup3 = BeautifulSoup(res,'lxml')
            if soup3.title:
                title = soup3.title.string
            else:
                title = 'NULL'
            #print('title:')
            t = title.split()
            cleaned_title = [word for word in t if  not containsNonAscii(word)]
            cleaned_title = " ".join(str(x) for x in cleaned_title)
            #print(cleaned_title)
            print(url)
            #print(title)
            #print('')
            texts = soup3.findAll(text=True)
            t = soup3.getText()
            t = t.replace('\\','')
            #print(t)
            ### removing non-ascii words
            words = t.split()
            #print words
    
            cleaned_words = [word for word in words if  not containsNonAscii(word)]
            cleaned_sentence = ' '.join(cleaned_words)
            cleaned_sentence = cleaned_sentence.replace('\\"','"')

            #print(cleaned_sentence)
        
            #out = open('doc['+str(i)+'].txt', 'w')
            #out.write(t)
            code = []
            if_code = []
            m1 = []
            m = re.findall('if\(.+?\)', cleaned_sentence)
            for i in m:
                i = i.lower()
                if 'jquery' not in i:
                    m1.append(i)
            if_code.extend(m1)

            m1 = []
            m = re.findall('if \(.+?\)', cleaned_sentence)
            for i in m:
                i = i.lower()
                if 'jquery' not in i:
                    m1.append(i)

            if_code.extend(m1)
            if len(if_code)>0:
                #print('found if')
                #print(if_code)
                code.extend(if_code)
        
            elseif_code = []
            m1 = []
            m = re.findall('else if\(.+?\)', cleaned_sentence)
            for i in m:
                i = i.lower()
                if 'jquery' not in i:
                    m1.append(i)

            elseif_code.extend(m1)
            m1 = []
            m = re.findall('else if \(.+?\)', cleaned_sentence)
            for i in m:
                i = i.lower()
                if 'jquery' not in i:
                    m1.append(i)

            elseif_code.extend(m1)
            if len(elseif_code)>0:
                #print('found else if')
                #print(elseif_code)
                code.extend(elseif_code)

            for_code = []
            m1 = []
            m = re.findall('for\(.+?\)', cleaned_sentence)
            for i in m:
                i = i.lower()
                if 'jquery' not in i:
                    m1.append(i)

            for_code.extend(m1)

            m1 = []
            m = re.findall('for \(.+?\)', cleaned_sentence)
            for i in m:
                i = i.lower()
                if 'jquery' not in i:
                    m1.append(i)

            for_code.extend(m1)
            if len(for_code)>0:
                #print('found for()')
                #print(for_code)
                code.extend(for_code)

            while_code = []
            m1 = []
            m = re.findall('while\(.+?\)', cleaned_sentence)
            for i in m:
                i = i.lower()
                if 'jquery' not in i:
                    m1.append(i)

            while_code.extend(m1)
            m1 = []
            m = re.findall('while \(.+?\)', cleaned_sentence)
            for i in m:
                i = i.lower()
                if 'jquery' not in i:
                    m1.append(i)

            while_code.extend(m1)
            if len(while_code)>0:
                #print('found while()')
                #print(while_code)
                code.extend(while_code)

            catch_code = []
            m1 = []
            m = re.findall('catch\(.+?\)', cleaned_sentence)
            for i in m:
                i = i.lower()
                if 'jquery' not in i:
                    m1.append(i)

            catch_code.extend(m1)

            m1 = []
            m = re.findall('catch \(.+?\)', cleaned_sentence)
            for i in m:
                i = i.lower()
                if 'jquery' not in i:
                    m1.append(i)

            catch_code.extend(m1)
            if len(catch_code)>0:
                #print('found catch()')
                #print(catch_code)
                code.extend(catch_code)



            #print(code)
            #print("")
            search_engine = "google"
        
            result = db.data.insert_one(
            {
            "query": query
            ,
            "title": cleaned_title
            ,
            "link": url
            ,
            "search_engine": search_engine
            ,
            "content": cleaned_sentence
            ,
            "code": code
            }
            )
            print(result.inserted_id)
            print("Google inserted"+str(j))
        except Exception as e:
            print("N/w error")
            print(e)


query = str(sys.argv[1])
print("in combined-google"+query)
#query = "null pointer exception"
#### caling function
#os.system('python stack_check.py')
#stack_search(query)
#stack_check.stack_search(query)

print('################')

google_search(query)



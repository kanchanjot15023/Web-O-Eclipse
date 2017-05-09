import stackexchange
import urllib, urllib2, re, urlparse,json
import requests
from bs4 import BeautifulSoup
from collections import defaultdict
so = stackexchange.StackOverflow()
from pymongo import MongoClient
import re
import sys
client = MongoClient()
db = client.test

def containsNonAscii(s):
    return any(ord(i)>127 for i in s)

def remove_tags(a):
    a = a.replace('<p>','')
    a = a.replace('\n','')
    a = a.replace('</p>','')
    a = a.replace('<a href>','')
    a = a.replace('</a>','')

    a = a.replace('<h1>','')
    a = a.replace('<h2>','')
    a = a.replace('<h3>','')
    a = a.replace('<h4>','')
    a = a.replace('<h5>','')
    a = a.replace('<h6>','')
    a = a.replace('<br>','')
    a = a.replace('<li>','')
    a = a.replace('<ul>','')
    a = a.replace('<em>','')
    a = a.replace('<code>','')
    a = a.replace('<pre>','')
    a = a.replace('<blockquote>','')
    a = a.replace('<strong>','')
    a = a.replace('<hr>','')

    a = a.replace('</h1>','')
    a = a.replace('</h2>','')
    a = a.replace('</h3>','')
    a = a.replace('</h4>','')
    a = a.replace('</h5>','')
    a = a.replace('</h6>','')
    a = a.replace('</br>','')
    a = a.replace('</li>','')
    a = a.replace('</ul>','')
    a = a.replace('</em>','')
    a = a.replace('</code>','')
    a = a.replace('</blockquote>','')
    a = a.replace('</strong>','')
    a = a.replace('</hr>','')
    a = a.replace('</pre>','')

    return a

def stack_search(query):
    inserted = 0
    #query = "null pointer exception"
    params = urllib.urlencode({'q': query, 'sort': 'relevance'})
    q = urllib.urlencode({'title':query})
    print(str(q))
    hdr = {'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11',
       'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8'}
    url = "https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=activity&tagged=java&"+str(q)+"&site=stackoverflow&key=CL0uzPtdBMegQDleHctSew(("
    url = "https://api.stackexchange.com/2.2/search/advanced?order=desc&sort=votes&nottagged=android&tagged=java&"+str(q)+"&site=stackoverflow&filter=!gNKMX_4n-svUN*uki.WhmKL9W8L7J0pGKtD&key=CL0uzPtdBMegQDleHctSew(("
    try:
        response = requests.get(url,hdr)
        f_json = response.json()
        ques_ids = []
        notAnswered = 0
        for i in range(len(f_json['items'])):
            title = f_json['items'][i]['title']
            t = title.split()
            cleaned_title = [word for word in t if  not containsNonAscii(word)]
            cleaned_title = " ".join(str(x) for x in cleaned_title)
            #print(cleaned_title)

            #print 'ans count::::',
            ans_count = f_json['items'][i]['answer_count']
            #print ans_count
            if ans_count != 0:
                ques_ids.append(f_json['items'][i]['question_id'])
        #   #print question_ids
        #print 'Question Ids'
        question_ids = str(ques_ids)
        question_ids = question_ids.replace(',',';')
        question_ids = question_ids.replace('[','')
        question_ids = question_ids.replace(']','')
        question_ids = question_ids.replace(' ','')
        question_ids = str(question_ids)
        #print question_ids
        #"+question_ids+"
        for j in range(len(ques_ids)):
            ques_code = []
            ans = defaultdict(list)
            ques_url = "https://api.stackexchange.com/2.2/questions/"+str(ques_ids[j])+"?order=desc&sort=activity&site=stackoverflow&filter=!)5bKRr_tykAHqDJPz.sps3WPOYee&key=CL0uzPtdBMegQDleHctSew(("
            response_ques = requests.get(ques_url)
            f_q_json = response_ques.json()
            #print 'QUES:::::'
            q = f_q_json['items'][0]['body']
            t = q.split()
            cleaned_q = [word for word in t if  not containsNonAscii(word)]
            cleaned_q = " ".join(str(x) for x in cleaned_q)
 
            ques_code = re.findall('<code>(.+?)</code>', cleaned_q)
            #print(ques_code)

            cleaned_q = remove_tags(cleaned_q)
            ques = cleaned_q
            link = f_q_json['items'][0]['link']
            is_answered = f_q_json['items'][0]['is_answered']
            #print 'Answered or not'
            #print is_answered
            ques_score = f_q_json['items'][0]['score']
            title = f_q_json['items'][0]['title']
            t = title.split()
            cleaned_title = [word for word in t if  not containsNonAscii(word)]
            cleaned_title = " ".join(str(x) for x in cleaned_title)
            #print(cleaned_title)

            li = []
            #print ques
            #print 'LINK:::'
            print link
            #print 'SCORE:::'
            #print score
            ans_url = "https://api.stackexchange.com/2.2/questions/"+str(ques_ids[j])+"/answers?order=desc&sort=votes&site=stackoverflow&filter=!0XMmcqRV*ZEGZ)0PzKVlDv1d*&key=CL0uzPtdBMegQDleHctSew(("
            response = requests.get(ans_url)
            f_json = response.json()
            #print len(f_json)
            #print "hhhhhhhhhhhhhhhhhh"
            #print f_json
            
            #print f_json['items'][0]
            #for j in range(len(f_json)):
            for i in range(len(f_json['items'])):
                inner = {}
                code = []
                #print i
                ques_id = f_json['items'][i]['question_id']
                #print ques_id
                score = f_json['items'][i]['score']
                #print score
                a = f_json['items'][i]['body']
                words = a.split()
                #print words
                cleaned_words = [word for word in words if  not containsNonAscii(word)]
                cleaned_sentence = ' '.join(cleaned_words)
                a = cleaned_sentence.replace('\\"','"')
                #a = str(a)
                code = re.findall('<code>(.+?)</code>', a)
                a = remove_tags(a)          
                inner['answer'] = a
                inner['score'] = score
                inner['code'] = code
                ans[i].append(inner)
                #li.append(a+','+score+','+code)
                #ans[i].append(li)
                li.append(inner)
        
            #print ans.items()

    
            search_engine = 'stackoverflow'
            #print is_answered
            #print type(is_answered)
            inserted = inserted+1
            result = db.data.insert_one(
            {
                "query":query
                ,
                "title": cleaned_title
                ,
                "search_engine": search_engine
                ,
                "question": ques
                ,
                "code_in_question":ques_code
                ,
                "link": link
                ,
                "score":ques_score
                ,
                "answers":li
            }
            )
            print(result.inserted_id)
            print("Stack inserted: "+str(inserted))
                                
    except Exception as e:
        print("N/w Error")
        print e
        if e == 'items':
            print('API limit exceeded.')
        sys.exit()
    print("Not answered questions = "+str(notAnswered))
    
query = str(sys.argv[1])
print("in stack_check"+query)
stack_search(query)

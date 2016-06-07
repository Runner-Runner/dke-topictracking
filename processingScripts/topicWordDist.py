#! /usr/bin/python

# extract single line / topic from the topic word distributions

import sys
from functools import cmp_to_key

def cmp(a, b):
    return (a > b) - (a < b)

if (__name__ == '__main__'):

    if (len(sys.argv) != 3):
       print('usage: python topicWordDist.py <beta-file> <topic index>\n')
       sys.exit(1)

    beta_file = sys.argv[1]
    topicId = int(sys.argv[2])

    f = open(beta_file, 'r').readlines()
    wordDist = f[topicId] 
    topic = list(map(float, wordDist.split()))
    indices = list(range(len(topic)))
    indices.sort(key=cmp_to_key(lambda x,y: -cmp(topic[x], topic[y])))
    #numToPrint = len(topic)#1000
    #officelimit = int(len(topic) / numToPrint)
    numToPrint = 222 #avg number of tokens per document RCV1
    for i in range(numToPrint):
        #print('%s ' % topic[indices[i * officelimit]], end="")
        print('%s ' % topic[indices[i]], end="")
    print("")

#    print(wordDist);


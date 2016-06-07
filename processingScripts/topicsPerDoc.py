#! /usr/bin/python

# usage: python topics.py <gamma-file> <num topics>
#
# <gamma file> is output from the lda-c code
# <num topics> is the number of topics to print from each document

import sys
from functools import cmp_to_key

def cmp(a, b):
    return (a > b) - (a < b)

def print_docs(gamma_file, ntopics = 3):

    # for each line in the gamma file

    #doc_no = 0
    f = open(gamma_file, 'r')

    for doc in f:
        #print('doc %03d' % doc_no)
        topic = list(map(float, doc.split()))
        indices = list(range(len(topic)))
        indices.sort(key=cmp_to_key(lambda x,y: -cmp(topic[x], topic[y])))
        for i in range(ntopics):
            if (topic[indices[i]] > 0.1):
                #print '   topic %s, weight %5f' % (indices[i], topic[indices[i]])
                print('%s:%8.6f ' % (indices[i], topic[indices[i]]), end="")
        print("")
        #doc_no = doc_no + 1
        #print '\n'

def print_docs_full(gamma_file):

    # for each line in the gamma file

    f = open(gamma_file, 'r')

    for doc in f:
        topic = list(map(float, doc.split()))
        for i in range(len(topic)):
            print('%s:%16.14f ' % (i, topic[i]), end="")
        print("")

if (__name__ == '__main__'):

    if (len(sys.argv) != 3):
       print('usage: python topics.py <gamma-file> <num topics>\n')
       sys.exit(1)

    gamma_file = sys.argv[1]
    ntopics = int(sys.argv[2])
    if ntopics == 0:
        print_docs_full(gamma_file)
    else:
        print_docs(gamma_file, ntopics)


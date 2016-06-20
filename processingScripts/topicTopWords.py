#! /usr/bin/python

# usage: python3 topicTopWords.py <beta file> <vocab file> <num words>
#
# <beta file> is output from the lda-c code
# <vocab file> is a list of words, one per line
# <num words> is the number of words to print from each topic

import sys
from functools import cmp_to_key

def cmp(a, b):
    return (a > b) - (a < b)

def print_topics(beta_file, vocab_file, nwords = 25):

    # get the vocabulary

    vocabular = open(vocab_file, 'r').readlines()
    vocab = list(map(lambda x: x.strip(), vocabular))

    # for each line in the beta file
    indices = list(range(len(vocab)))
    topic_no = 0
    f = open(beta_file, 'r')
    for topic in f:
        topic = list(map(float, topic.split()))
        indices.sort(key=cmp_to_key(lambda x,y: -cmp(topic[x], topic[y])))
        for i in range(nwords):
            #print('%s ' % vocab[indices[i]], end="")
            print('%s:%f ' % (vocab[indices[i]], topic[indices[i]]), end="")
        print("")

if (__name__ == '__main__'):

    if (len(sys.argv) != 4):
       print('usage: python3 topicTopWords.py <beta-file> <vocab-file> <num words>\n')
       sys.exit(1)

    beta_file = sys.argv[1]
    vocab_file = sys.argv[2]
    nwords = int(sys.argv[3])
    print_topics(beta_file, vocab_file, nwords)

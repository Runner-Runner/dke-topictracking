#! /usr/bin/python

# usage: python3 topicsFromDTM.py <outputdir> <num timesteps>
#
# <outputdir> is output directory of dtm containing lda-seq subdirectory
# <num timesteps> is the number of timesteps dtm was run with

import sys
from math import exp
import glob

def print_topics(outputdir, ntimesteps):

    betas = glob.glob(outputdir + '/lda-seq/topic-???-var-e-log-prob.dat')
    ntopics = len(betas)

    
    topic_no = 0
    for beta_file in betas:
        beta = open(beta_file, 'r').readlines()
        topic_str = map(lambda x: x.strip(), beta)
        topic = list(map(float, topic_str))
        lwords = len(topic)//ntimesteps
        for i in range(ntimesteps):
            topic_t = list();
            for j in range(i, len(topic), ntimesteps):
                x = exp(topic[j])
                topic_t.append(x)
            for u in range(len(topic_t)):
                print('%s ' % topic_t[u], end = "")
            print("")
        topic_no += 1


if (__name__ == '__main__'):

    if (len(sys.argv) != 3):
       print('usage: python3 topicsFromDTM.py <outputdir> <num timesteps>\n')
       sys.exit(1)

    outputdir = sys.argv[1]
    ntimesteps = int(sys.argv[2])
    print_topics(outputdir, ntimesteps)


#! /usr/bin/python

import sys
import glob
from scipy import spatial

if (__name__ == '__main__'):

    if (len(sys.argv) != 1):
       print('usage: python topicSimilarity.py\n')
       sys.exit(1)

    # load topic timesteps for given topic
    betas = glob.glob('topic0_t?.txt')
    ntimesteps = len(betas)
    print('number of timesteps: %i' % ntimesteps)

    topicT = 0
    topicTimestep = list();
    for beta_file in betas:
        beta = open(beta_file, 'r').readline()
        topic = list(map(float, beta.split()))
        nWords = len(topic)
        print('number of words in %s: %i' % (beta_file, nWords))
        topicTimestep.append(topic)
        topicT = topicT + 1

    topicT = 0
    for topic in range(len(topicTimestep) - 1):
        topicTNext = topicT + 1
        sim = 1 - spatial.distance.cosine(topicTimestep[topicT], topicTimestep[topicTNext])
        print('sim between %i and %i = %f' % (topicT, topicTNext, sim))
        topicT = topicT + 1

#    sim = 1 - spatial.distance.cosine(topicTimestep[0], topicTimestep[ntimesteps - 1])
#    print('sim between 0 and %i = %f' % (ntimesteps - 1, sim))


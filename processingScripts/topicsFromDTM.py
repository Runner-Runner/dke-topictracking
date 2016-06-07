#! /usr/bin/python

import sys
from math import exp
import glob

def print_topics(beta_file_pattern, ntimesteps):

    betas = glob.glob('out/lda-seq/topic-???-var-e-log-prob.dat')
    ntopics = len(betas)

    
    topic_no = 0
    for beta_file in betas:
        beta = open(beta_file, 'r').readlines()
        topic_str = map(lambda x: x.strip(), beta)
        topic = list(map(float, topic_str))
        lwords = len(topic)//ntimesteps
        #out_filename = "topic" + str(topic_no)
        #out = open(out_filename, 'w', encoding = 'utf-8')
        #output = ""
        for i in range(ntimesteps):
            topic_t = list();
            for j in range(i, len(topic), ntimesteps):
                x = exp(topic[j])
                topic_t.append(x)
            # write topic, line = timestep
            for u in range(len(topic_t)):
                print('%s ' % topic_t[u], end = "")
                #output += str(topic_t[u]) + " "
#                out.write('%s ' % topic_t[u])
            #output += "\n"
            print("")
#            out.write('\n')
        #out.write(output)
        #out.close()
        topic_no += 1
            
#        for t in range(ntimesteps):
#            for w in range(lwords):
#                topic_t.append(topic[])
#
#
 #       for t in range(ntimesteps):
 #           topic_t = topic[(t * lwords):((t + 1) * lwords)]
#            indices.sort(key=cmp_to_key(lambda x,y: -cmp(exp(topic_t[x]), exp(topic_t[y]))))
#            out_filename = "topic" + str(topic_no) + "_t" + str(t)
#            out = open(out_filename, 'w')
#            for i in range(nwords):
#                out.write('%s ' % vocab[indices[i]])
#            out.close()
#        topic_no += 1
        

#x = -13.9616538233468130
#y = exp(x);
#print(y)


if (__name__ == '__main__'):

    if (len(sys.argv) != 3):
       print('usage: python topicsFromDTM.py <beta-file-pattern> <num timesteps>\n')
       sys.exit(1)

    beta_file_pattern = sys.argv[1]
    ntimesteps = int(sys.argv[2])
    print_topics(beta_file_pattern, ntimesteps)

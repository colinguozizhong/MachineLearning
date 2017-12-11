import twitter
import os

def saveData(nbaData):
    i = 0
    for item in nbaData:
        dataFile = os.path.join('/tmp/tw/', str(item.id))
        f = open(dataFile, 'w')
        try:
            f.write(item.text)
            f.close()
            i += 1
        except:
            f.close()
            os.remove(dataFile)
            continue
    print "Saved %d files" % i

consumer_key='Your consumer_key'
consumer_secret='Your consumer_secret'
access_token_key='Your access_token_key'
access_token_secret='Your access_token_secret'
api = twitter.Api(consumer_key, consumer_secret, access_token_key, access_token_secret)

nbaData = api.GetSearch('NBA', result_type='recent', count=100)
last_id = nbaData[99].id
page = 1

saveData(nbaData)

while page < 20:
    nbaData = api.GetSearch('NBA', max_id=last_id, result_type='recent', count=100)
    length = len(nbaData)
    last_id = nbaData[length-1].id
    page += 1
    saveData(nbaData)


import json
import os
from datetime import datetime

from dotenv import load_dotenv
from garminconnect import Garmin

load_dotenv()

email = os.getenv('EMAIL')
password = os.getenv('PASSWORD')

client = Garmin(email, password)

try:
    client.login()
    print('Login successful.')

    today = datetime.today().strftime('%Y-%m-%d')
    print(f'Today is: {today}')

    start = 1
    limit = 100
    path = f'adhocchallenge-service/adHocChallenge/nonCompleted?start={start}&limit={limit}'
    active_challenges = client.connectapi(path)
    if len(active_challenges) != 0:
        print(f'Active Challenges found: {len(active_challenges)}')
        for challenge in active_challenges:
            uuid = challenge["uuid"]
            print(f'Challenge name: {challenge["adHocChallengeName"]} [{uuid}]')
            chl = client.connectapi(f'adhocchallenge-service/adHocChallenge/{uuid}?todayDate={today}')
            print(json.dumps(chl))
except Exception as e:
    print(f'An error occurred: {e}')

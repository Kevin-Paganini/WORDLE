import requests
import time

url = "http://localhost:8000"
body = {"Type":"Global", "Target": "BOOBY", "User":"paganinik", "Guesses":"EARTH SPACE ARROW BOOBY", "Number_of_guesses": "4", "Win":"Yes", "Game_Number": "4"}




for i in range(50):
    x = requests.post(url, data=body)

    print(x.text)
    time.sleep(5)




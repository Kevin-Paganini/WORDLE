import requests
import time

url = "http://localhost:8000"
body = {"Type":"Global", "User":"paganinik", "Game_Number": "4", "Target": "BOOBY", "Number_of_guesses": "4", "Win":"Yes", "Guesses":"EARTH SPACE ARROW BOOBY"}





x = requests.post(url, data=body)

print(x.text)



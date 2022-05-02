import requests

url = "http://localhost:80"
body = {"foo": "bar"}

x = requests.post(url, data=body)

print(x.text)



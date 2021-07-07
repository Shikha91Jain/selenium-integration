# Selenium Integration Application

Basic spring boot application integrating with AWS SQS and Selenium for google search

Application provides below functionality:
* API interface to send message to SQS queue
* SQS Listener which listens to the SQS queue and performs google search based on the payload received from the SQS queue. Payload also contains the browser name to indicate the test needs to be run in which browser.


# Sample Requests & Response
## Send message to SQS topic
### Request
```
POST http://localhost:8080/send

Content-Type: application/json
Host: localhost:8080

{
	"search":"Google",
	"browser":"chrome"
}
```
### Response
```
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8

{"status":"Success"}
```

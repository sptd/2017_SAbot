# 2017_SAbot

Schedule Adjustment Chatbot 
==================

This bot is SAbot(Schedule Adjustment Chatbot).
SAbot confirms team member’s Schedule ,and accordingly adjusts Schedule.
No matter where you are, will be able to confirms team member’s Schedule.
Let’s using to confirms team member’s Schedule !

Steps to build the Moviepedia Bot 
---------------------------------

## Step 1: Create the AWS Lambda function
Use this end point to upload the lambda code from s3:
https://s3.amazonaws.com/lambda-function-bucket-us-east-1-1498394635267/ScheduleAjustmentBotlambdaFunction_v3.zip

The lambda function uses Java SE-1.8, AWS SDK for Java(1.11.153), AWS lambda Java Function Runtime(1.1), and google api jar files(1.22.0).
The google api jar files is are contains lib/ in this repository.
Instructions: 
-------------
##### Configure Function: 
      1. Name your lambda function : ScheduleAjustmentBotlambdaFunction_v3
      2. Environment function :       
            (i)Bucket name, which contains "userInfo/user.properties" and "apiKey/googleAPIKey.json".      
            KEY : BACKET_NAME      
            VALUE :Created bucket Name      
            (ii)Time Zone ID      
            KEY : TZ
            VALUE :a short ID adapts your Google Calendar time zone[(ex)Asia/Tokyo]
      3. Runtime - Java 8
      4. Code Entry - Upload the zip you downloaded
      5. Handler Section - sabot.LambdaFunctionHandler
      6. Select an existing role -  LexlambdaS3Role
      7. Set time to 15 secs

##### Function Trriger: 

      {
          "Version": "2012-10-17",
          "Id": "default",
          "Statement": [
              {
                  "Sid": "lex-us-east-1-Reference",
                  "Effect": "Allow",
                  "Principal": {
                      "Service": "lex.amazonaws.com"
                  },
                  "Action": "lambda:invokeFunction",
                  "Resource": "arn:aws:lambda:us-east-1:818867563308:function:ScheduleAjustmentBotlambdaFunction_v3",
                  "Condition": {
                      "ArnLike": {
                          "AWS:SourceArn": "arn:aws:lex:us-east-1:818867563308:intent:Reference:*"
                      }
                  }
              },
              {
                  "Sid": "lex-us-east-1-Add",
                  "Effect": "Allow",
                  "Principal": {
                      "Service": "lex.amazonaws.com"
                  },
                  "Action": "lambda:invokeFunction",
                  "Resource": "arn:aws:lambda:us-east-1:818867563308:function:ScheduleAjustmentBotlambdaFunction_v3",
                  "Condition": {
                      "ArnLike": {
                          "AWS:SourceArn": "arn:aws:lex:us-east-1:818867563308:intent:Add:*"
                      }
                  }
              }
          ]
      }

#### Test your lambda function. 

Configure the following test event to test your lambda function.

      {
          "messageVersion": "1.0",
          "invocationSource": "DialogCodeHook",
          "userId": "be7df995-1361-420a-a4c1-541613b6bd95:T5HPQH8VA:U5JHEFMQF",
          "sessionAttributes": {},
          "bot": {
              "name": "ScheduleAdjustment",
              "alias": null,
              "version": "6"
          },
          "outputDialogMode": "Text",
          "currentIntent": {
              "name": "Add",
              "slots": {
                  "EndTime": null,
                  "StartTime": null,
                  "Title": null,
                  "Member": null,
                  "Date": null,
                  "Location": null
              },
              "confirmationStatus": "None"
          },
          "inputTranscript": "add"
      }

#### The output should look like this: 

      {
          "sessionAttributes": {},
          "dialogAction": {
              "type": "ElicitSlot",
              "intentName": "Add",
              "slots": {
                  "EndTime": null,
                  "StartTime": null,
                  "Title": null,
                  "Member": null,
                  "Date": null,
                  "Location": null
              },
              "slotToElicit": "Member"
          }
      }



## Step 2: Creating your Bot and Creating your Bot Conversations
      
      Please read https://github.com/sptd/2017_SAbot/blob/master/doc/SAbot_HowTo.pdf in this repository.This repository also contains the SAbot definition file https://github.com/sptd/2017_SAbot/blob/master/SAbot.json , which is export by https://gist.github.com/nhira/3036fdc49f51e838f1beea8dcb7c1fa3
       


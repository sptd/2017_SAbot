# 2017_SAbot

## Step 1: Create the AWS Lambda function
Use this end point to upload the lambda code from s3:
https://s3.amazonaws.com/lambda-function-bucket-us-east-1-1498394635267/ScheduleAjustmentBotlambdaFunction_v3.zip

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



## Step 2: Creating your Bot

#### 1. Create Amazon Lex Bot 

      Open the Amazon Lex console at https://console.aws.amazon.com/lex/.
      On the Bots page, choose Create .
      Select custom bot and provide the following information, then choose Create.
      Provide The Following Information:
        1. Bot Name: ScheduleAdjustment
        2. Choose an output voice - None. This is only a text based application.
        3. Set Session Timeout - 5 mins
        4. Add AMazon lex basic role to your Bot app - AWSServiceRoleForLexBots
        
![MacDown Screenshot](https://user-images.githubusercontent.com/30132513/28303779-bab76c52-6bcf-11e7-86e7-77fe83c30bc9.png)

## Step 3: Creating your Bot Conversations

####2. Create Slots

      In this section you create the three slots type for ChatBot.
      
####Create your first slot

      First slot type is for to input the user you want to check the schedule.
      Slot Type Name : Users
      
![MacDown Screenshot](https://user-images.githubusercontent.com/30132513/28303872-26568042-6bd0-11e7-9eda-b7de54cc183b.png)
      




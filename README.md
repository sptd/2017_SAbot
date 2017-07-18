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
      4. Set time to 15 secs

#### 5. Test your lambda function. 

Configure the following test event to test your lambda function.

    {
    "messageVersion": "1.0",
    "invocationSource": "FulfillmentCodeHook",
    "userId": "user-1",
    "sessionAttributes": {},
    "bot": {
    "name": "movieInfoApp",
    "alias": "$LATEST",
    "version": "$LATEST"
    },
    "outputDialogMode": "Text",
    "currentIntent": {
    "name": "movieInfo",
    "slots": {
      "name": "Suicide Squad",
      "summary": "Director"
      },
    "confirmationStatus": "None"
     }
    }

#### The output should look like this: 

    {
     "sessionAttributes": {},
     "dialogAction": {
     "type": "Close",
     "fulfillmentState": "Fulfilled",
     "message": {
       "contentType": "PlainText",
       "content": "Director of Suicide Squad is/are: David Ayer"
       }
     }
    }



## Step 2: Creating your Bot

## Step 3: Creating your Bot Conversations


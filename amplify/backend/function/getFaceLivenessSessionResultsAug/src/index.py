import json
import boto3

client = boto3.client('rekognition')

def get_session_results(session_id):

    response = client.get_face_liveness_session_results(SessionId=session_id)

    confidence = response.get("Confidence")
    status = response.get("Status")

    print('Confidence ' + "{:.2f}".format(confidence) + "%")
    print('Status: ' + status)

    return response

def handler(event, context):
    print('received event:')
    print(event)

    res = get_session_results(event.get("Sessionid"))

    return {
        'statusCode': 200,
        'headers': {
            'Access-Control-Allow-Headers': '*',
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'OPTIONS,POST,GET'
        },
        'body': json.dumps(res)
    }

import json
import boto3
import uuid

def create_session():
    client = boto3.client('rekognition')
    response = client.create_face_liveness_session(
        KmsKeyId="f9a5afc3-1ec0-42f1-b56b-05c338ca6194",
        Settings={
            'OutputConfig': {
                'S3Bucket': 'amplify-face-rekog',
                'S3KeyPrefix': 's3-face-rekog-prefix'
            },
            'AuditImagesLimit': 1
        },
        ClientRequestToken= str(uuid.uuid4())
    )
    session_id = response.get("SessionId")
    return {'session_id': session_id, 'region': 'us-east-1'}

def handler(event, context):
    print('received event:')
    print(event)

    # boto3Vers = (boto3.__version__)

    return {
        'statusCode': 200,
        'headers': {
            'Access-Control-Allow-Headers': '*',
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'OPTIONS,POST,GET'
        },
        'body': json.dumps(create_session())
    }

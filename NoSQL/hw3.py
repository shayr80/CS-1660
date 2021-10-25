import boto3

s3 = boto3.resource('s3',
                   aws_access_key_id='YOUR_ACCESS_KEY',
                   aws_secret_access_key='YOUR_SECRET_KEY')

try:
    s3.create_bucket(Bucket='shr80-hw3-cs1660', CreateBucketConfiguration={
        'LocationConstraint': 'us-west-2'
    })
except Exception as e:
    print(e)

bucket = s3.Bucket("shr80-hw3-cs1660")

bucket.Acl().put(ACL='public-read')

body = open('experiments.csv', 'rb')
o = s3.Object('shr80-hw3-cs1660', 'experiments').put(Body=body)
s3.Object('shr80-hw3-cs1660', 'experiments').Acl().put(ACL='public-read')

dyndb = boto3.resource('dynamodb',
                      region_name='us-west-2',
                      aws_access_key_id='YOUR_ACCESS_KEY',
                      aws_secret_access_key='YOUR_SECRET_KEY')

try:
    table = dyndb.create_table(
        TableName='shr80-hw3-datatable',
        KeySchema=[
            {
                'AttributeName': 'PartitionKey',
                'KeyType': 'HASH'
            },
            {
                'AttributeName': 'RowKey',
                'KeyType': 'RANGE'
            }
        ],
        AttributeDefinitions=[
            {
                'AttributeName': 'PartitionKey',
                'AttributeType': 'S'
            },
            {
                'AttributeName': 'RowKey',
                'AttributeType': 'S'
            },
        ],
        ProvisionedThroughput={
            'ReadCapacityUnits': 5,
            'WriteCapacityUnits': 5
        })
except Exception as e:
    print(e)
    table = dyndb.Table("shr80-hw3-datatable")

table.meta.client.get_waiter('table_exists').wait(TableName='shr80-hw3-datatable')

import csv

with open('experiments.csv', 'rt') as csvfile:
    csvf = csv.reader(csvfile, delimiter=',', quotechar='|')
    next(csvf)
    for item in csvf:
        print (item)
        body = open(item[5], 'rb')
        s3.Object('shr80-hw3-cs1660', item[5]).put(Body=body)
        md = s3.Object('shr80-hw3-cs1660', item[5]).Acl().put(ACL='public-read')
        
        url = "https://shr80-hw3-cs1660.s3.us-west-2.amazonaws.com/"+item[5]
        metadata_item = {'PartitionKey': item[0], 'RowKey': item[1], 'Temp': item[2], 
                         'Conductivity': item[3], 'Concentration': item[4], 'URL':url}
        try:
            table.put_item(Item=metadata_item)
        except:
            print ("item may already be there or another failure")

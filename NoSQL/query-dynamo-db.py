import boto3

dyndb = boto3.resource('dynamodb',
                      region_name='us-west-2',
                      aws_access_key_id='YOUR_ACCESS_KEY',
                      aws_secret_access_key='YOUR_SECRET_KEY')

table = dyndb.Table("shr80-hw3-datatable")
table.meta.client.get_waiter('table_exists').wait(TableName='shr80-hw3-datatable')

response = table.get_item(Key={
    'PartitionKey': 'experiment 2',
    'RowKey': '2'
})
item = response['Item']
print("\n")
print(item)
print("\n")
print(response)
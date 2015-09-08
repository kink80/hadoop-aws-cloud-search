# hadoop-aws-cloud-search

This project contains a Cascading framework tap that connects to the AWS Cloud Search.

See ProxyIndexTest class for usage, to get it working create a plain text file that has a single word on each line. If correctly set it will index that many documents into AWS cloudsearch as there were in the input file.

The mapping between incoming and outgoing tuple can be configured in config.properties - see rest.mapper.fields. Predefined example maps content of the second element to a field name content of the output.

To get this working 
 - you need to setup a Cloud Search at AWS first (https://docs.aws.amazon.com/cloudsearch/latest/developerguide/getting-started-create-domain.html, https://docs.aws.amazon.com/cloudsearch/latest/developerguide/creating-domains.html)
 - configure index fields (https://docs.aws.amazon.com/cloudsearch/latest/developerguide/configuring-index-fields.html)
 - fill AWS access credentials into config.properties
 - fill Cloud Search domain to use in config.properties
 - set correct mapping in config.properties
 - 
 

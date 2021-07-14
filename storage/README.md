Access to Amazon S3
===================

The AWS credentials are provided by the 
[DefaultCredentialsProvider](http://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/auth/credentials/DefaultCredentialsProvider.html)
component.

The region for accessing the AWS services is provided by the
[DefaultAwsRegionProviderChain](http://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/regions/providers/DefaultAwsRegionProviderChain.html)
component.

The App archives the material in the S3 bucket `it-scoppelletti-articles`. The
name of the bucket can be ovverriden by setting it to the environment variable
`KB_BUCKET`.


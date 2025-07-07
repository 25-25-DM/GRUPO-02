package com.example.gacs_wheel.Controller

import com.amazonaws.auth.BasicSessionCredentials
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.regions.Region
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.services.s3.AmazonS3Client

object AwsClientProvider {
    fun provideDynamoDBMapper(
        accessKey: String,
        secretKey: String,
        sessionToken: String
    ): DynamoDBMapper {
        val credentials = BasicSessionCredentials(accessKey, secretKey, sessionToken)
        val client = AmazonDynamoDBClient(credentials)
        client.setRegion(Region.getRegion("us-east-1"))
        return DynamoDBMapper(client)
    }

    fun provideS3Client(
        accessKey: String,
        secretKey: String,
        sessionToken: String
    ): AmazonS3Client {
        val credentials = BasicSessionCredentials(accessKey, secretKey, sessionToken)
        val s3 = AmazonS3Client(credentials)
        s3.setRegion(Region.getRegion("us-east-1"))
        return s3
    }
}

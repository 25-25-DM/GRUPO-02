package com.example.gacs_wheel.Controller

import com.amazonaws.auth.BasicSessionCredentials
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.amazonaws.regions.Regions

object AwsClientProvider {
    fun provideDynamoDBMapper(
        accessKey: String,
        secretKey: String,
        sessionToken: String
    ): DynamoDBMapper {
        val credentials = BasicSessionCredentials(accessKey, secretKey, sessionToken)
        val client = AmazonDynamoDBClient(credentials)
        client.setRegion(com.amazonaws.regions.Region.getRegion("us-east-1")) // Ajusta tu región
        return DynamoDBMapper(client)
    }
}
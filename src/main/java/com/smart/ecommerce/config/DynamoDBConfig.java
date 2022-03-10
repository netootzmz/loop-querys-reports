/* ----------------------------------------------------------------------------
 * All rights reserved Smart Payment Services.
 *  
 * This software contains information that is exclusive property of Smart,this 
 * information is considered confidential.
 * It is strictly forbidden the copy or spreading of any part of this document 
 * in any format, whether mechanic or electronic.
 * ---------------------------------------------------------------------------
 */
package com.smart.ecommerce.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;

/**
 * <code>DynamoDBConfig</code>.
 *
 * @author Adrian Pantoja
 * @version 1.0
 */
@Component
public class DynamoDBConfig {

  /**
   * Dynamo DB mapper.
   *
   * @return dynamo DB mapper
   */
  @Bean
  public DynamoDBMapper dynamoDBMapper() {

    AmazonDynamoDB client = getProviderAmazonDynamoDB();
    return new DynamoDBMapper(client, DynamoDBMapperConfig.DEFAULT);
  }

  /**
   * Dynamo DB.
   *
   * @return dynamo DB
   */
  @Bean
  public DynamoDB dynamoDB() {

    AmazonDynamoDB client = getProviderAmazonDynamoDB();
    return new DynamoDB(client);
  }

  /**
   * Gets the provider amazon dynamo DB.
   *
   * @return provider amazon dynamo DB
   */
  private AmazonDynamoDB getProviderAmazonDynamoDB() {

    return AmazonDynamoDBClientBuilder.standard()
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
        "AKIAQNLG57LPTHNHM3FW", "SEH4Co298aXVfVq7e66Y4sRwAZHZNk9Yh7c1zr7o")))
      .withRegion(Regions.US_EAST_1).build();
  }

  /**
   * Client amazon dynamo DB.
   *
   * @return amazon dynamo DB
   */
  @Bean
  public AmazonDynamoDB clientAmazonDynamoDB() {

    return getProviderAmazonDynamoDB();
  }

  /*
  '@'Bean
  'public' ObjectMapper objectMapper'()' '{'
    return new ObjectMapper()
  '}'
  */

}

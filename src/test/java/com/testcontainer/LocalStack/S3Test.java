package com.testcontainer.LocalStack;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
class S3Test {
	private static DockerImageName localstackImage = DockerImageName.parse("localstack/localstack");

	@Container
	public static LocalStackContainer localstack;

	private static AmazonS3 s3;

	static {
		localstack = new LocalStackContainer(localstackImage).withServices(LocalStackContainer.Service.S3);

		localstack.start();
		s3 = AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(localstack.getEndpointConfiguration(LocalStackContainer.Service.S3))
				.withCredentials(localstack.getDefaultCredentialsProvider()).build();
	}

	@Test
	void objectListTest() {
		save();
		ObjectListing objectListing = s3.listObjects("foo");
		List<S3ObjectSummary> objectSummaries = objectListing.getObjectSummaries();
		assertThat(objectSummaries.size()).isEqualTo(2);

	}

	@Test
	void bucketListTest() {
		save();
		List<Bucket> buckets = s3.listBuckets();
		assertThat(buckets.size()).isEqualTo(1);
	}

	private void save() {
		s3.createBucket("foo");
		s3.putObject("foo", "bar", "baz");
		s3.putObject("foo", "bar1", "baz1");

	}

	@AfterAll
	public static void after() {
		localstack.stop();
	}
}

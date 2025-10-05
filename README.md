# SpringBatch

## Project Description
Example of a simple Spring Batch implementation to be used as a reference for more complicated data enrichment batch processes.

## Technologies Used
![](https://img.shields.io/badge/-Java-007396?style=flat-square&logo=java&logoColor=white)
![](https://img.shields.io/badge/-Spring_Boot-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)
![](https://img.shields.io/badge/-PostgreSQL-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Maven](https://img.shields.io/badge/-Maven-C71A36?style=flat-square&logo=apache-maven&logoColor=white)


## Features
* Utilizes an async JobLauncher to acknowledge request immediately without having to wait for long-running batch process to complete.
* Implements JPA paging item reader to process data in manageable chunks.
* Automatic retry logic for recoverable exceptions.
* Simple. Easy to understand but more meaningful and extendible than a "Hello" application.

TrafficMapper:

This is a simple program to parse a file containing flow log data and map each log to a tag based on a lookup table. The lookup table is defined as a CSV file and has 3 columns - destination port, protocol and tag. The destination port and protocol combination decide what tag can be applied. The combination of destination port and protocol is referred to as Traffic Type throughout the repo. 

The program consumes these 2 files and generates an output file which has the below info:

1. Count of matches for each tag: Key is the tag and value is a number indicating the number of times the tag was matched with a flow log
2. Count of matches for each Port-Protocol combination: Key is traffic type and value is the number indicating the number of times the traffic type was matched with a flow log

Assumptions/Constraints

1. TrafficMapper only supports parsing of version 2 flow logs with Default format. More details about flow logs: https://docs.aws.amazon.com/vpc/latest/userguide/flow-log-records.html 
2. The lookup table needs to be a CSV file with 3 columns - destination port, protocol and tag. The first line of the lookup table has the names of the columns. 
3. Both these files and the output file generated are all ascii text files. 
4. The program expects 3 arguments, 1st argument is the path to the flow log file including the log file name. Second arg is the file name with path of the lookup table (CSV file) and the 3rd argument is the desired file name and path for the output file.
5. All paths are wrt the TrafficMapper home directory. 
6. I have a very limited set of protocol numbers to name mappings statically defined in 
`IANAProtocolMap` . Took a few protocols that were present in the sample data and used them. So while testing, we need to ensure that new protocols are added to this file beforehand, and then run the program with the log file. The current protocol numbers are only sufficient to run the end to end UT (more details on this UT below)
7. Maven is required to build and run the program
8. Maven needs configured to use Open JDK 22.

Steps to build and run the program

1. “mvn clean package” - This will generate a jar file inside target folder named “TrafficMapper-1.0-SNAPSHOT-jar-with-dependencies.jar”. This includes all external and internal dependencies. 
2. Run the program: java -cp target/TrafficMapper-1.0-SNAPSHOT-jar-with-dependencies.jar org.simple.App ./path/to/log/file/logfilename.txt ./path/to/lookuptable/lookuptable.csv ./path/where/output/file/will/be/generated/outputfilename.txt
    1. Ex: java -cp target/TrafficMapper-1.0-SNAPSHOT-jar-with-dependencies.jar org.simple.App ./src/test/resources/testFlowLogFile.txt ./src/test/resources/testLookupTable.csv programOutputFile.txt

Design thoughts:

Since this program involves just 4 class files , no java framework is used. Its just plain java. 

I have also added a basic version of unit test files for each of these java classes. I have wrapped up the source files, UT files and dependencies using maven due to its ease of use. 

I have tried to limit the knowledge of the flow log format and its specifics to 
`DefaultV2LogParser` class. The idea is to make minimal changes to TrafficMapper in future, when additional parsers are added, for example a parser for custom format. There could be a common LogParser interface that TrafficMapper could use. 

Similarly, the knowledge of the lookup table, its format is limited to 
`TrafficTypeToTagMapping` class. It also owns the cache. It helps with building keys that goes in as part of its cache. 

Since log files can get really big, up-to 10Mb, loading the entire log file into memory might not be the best thing to do. Hence I have used BufferedReader to read line by line of the file and not load the entire log file into the memory. However, since lookup table is relatively small (up-to 10k keys), it makes sense to have it as cache as a one time operation. Also for every log file read, it would be inefficient to go over all the lookup keys to match the log. 

Protocol number to name mapping: This is done in a very poor way due to time constraints. The protocol numbers to name mapping is statically defined for only a few set of protocols. This obviously needs to change and will have to figure out a more comprehensive way to support all protocols without statically defining them. 

Testing:

I have added only few basic UTs. There is one end to end UT which uses sample flow logs and sample lookup table files with valid data and generates the output. The “./src/test/resources” folder has “testFlowLogFile.txt” which is the log file, “testLookupTable.csv” which is the lookup table. As part of the build process, this UT is run and it generates the output file named “happyPathOutput.txt”.

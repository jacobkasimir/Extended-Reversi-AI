# group 5

**🛠️ Instructions for Compiling and Running the Client**


**🔧 Compilation**
The project is implemented in Java and organized using the following directory structure:

/src      → Source code  
/bin      → Compiled classes and executable .jar  
/maps     → Game maps  
/reports  → LaTeX reports
/presentations → Presentations on our progress

The compiled .jar files lies in the /bin directory

**📀 Compiling Create .jar file**
To compile the project and generate the .jar file, use the following command from the project root:

`gradle assemble`

**🚀 Running the Client**
To run the client, navigate to the project root and use the following command:

`java -jar bin/swp2025_group5.jar --server <host> --port <port>`

🔤 Available Command-Line Flags
`--help / -h`: Show help information with descriptions of all available flags.

`--server <hostname/IP> / -s`: Specify the hostname or IP address (IPv4/IPv6) of the server to connect to.

`--port <number> / -p`: Specify the port of the server (e.g., 12345).

✅ Example Usage
`java -jar bin/GameClient.jar -s 127.0.0.1 -p 12345`
This connects the client to a local game server running on port 12345.
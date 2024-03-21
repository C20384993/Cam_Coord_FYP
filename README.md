Ciaran MacDermott
C20384993
4th Year Final Project

Requirements
Cam-Coord REST API 	 		(https://github.com/C20384993/Cam_Coord_REST_API)
Cam-Coord Path Adder Tool	(https://github.com/C20384993/Cam_Coord_Path_Adder_Tool)
MediaMTX			 		(https://github.com/bluenviron/mediamtx)

Description
For my Final Year Project, I have made a IP camera management tool. This is an Android Application that allows the user to enter IP camera details
and then view, and create recordings from, the footage of the camera. Recordings are stored in Azure Cloud Storage and can be downloaded and 
managed by the user through the app. The applcation can connect directly to the RTSP streams of the added IP Cameras to view footage, and through
the use of MediaMTX (https://github.com/bluenviron/mediamtx) and an Azure Virtual Machine, camera footage can be viewed remotely.

This Android Application is the project front-end.  My other parts of the system and MediaMTX, as listed above in the requirements section,
are needed to form the complete system.

I developed a Spring Boot REST API for the middle-tier of the system, to provide records from a SQL database to the front-end.

The backend consists of a Azure SQL database, Virtual Machine to host the REST API and MediaMTX server, and Azure Blob Storage. 



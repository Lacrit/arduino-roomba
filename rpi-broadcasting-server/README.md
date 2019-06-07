# Manual

The user communicates with **Control Server** by providing robot type numbers and 3-character long input commands.
**Control Server** then parses user input and sends the command to the **Subordinate Server** running on the 
corresponding robots's Raspberry Pi system within the local network. 

### Robot types

Used internally as the **sender_type** and used from terminal for **Control Server** as **receiver_type** 
(the type of the device to which the command is being sent)


* ``0 - Broadcast`` 
* ``1 - Control server ("the brain")``  
* ``2 - CameraRobot ``
* ``3 - The other robot  ``


## Command format (communication between user and Control server)

The format of the input command.

``[target_device_type] + [cmd]``

### Command format (communication between servers)

This is the format of the command being exchanged over the network between **Control Server** and **Subordinate Servers**.

>``[sender_type] + [cmd] + ((cmd == ACK)? [cmd2]: empty) + [sender_ip]``
>  
> Please note that:  
>* **cmd** and **cmd2** is always 3-character long   
>* if **cmd** is ``ACK`` then **cmd2** is the command that was sent to the sender  

## Protocol 

### Shared

``END``: terminates launcher

### Control Server 

`SYN`: creates new entry for the sender  
`DEL`: deletes entry of the sender

### Subordinate server

`ACKSYN` stores the IP of the sender as **Control Server**

## Adding new command

The commands are stored in `UDPServer`'s `commands` attribute. The responses to commands are stored in 
`UDPServer`'s `ackResponses` attribute. Both are dictionaries mapping strings to function calls. 

In order to add
a new command one should first create a constant in `Util` (to prevent typos and simplify refactorings):
> `public static String SYN = "SYN";`

Then in the constructor of the `ControlServer` or `SubordinateServer` bind this string value to a function:
> `commands.put(Util.SYN, p -> createNewEntry(p));`

The method should take all data from the packet as a string parameter.
In that method the string data from the packet is supposed to be parsed using predetermined format 
(see Command format section). If the receiver should give `ACK` response back to the sender, 
the sender should have a new entry in their `ackResponses` dictionary (analogous to `commands`):
>  `ackResponses.put(Util.SYN, p -> synACKResponse(p));`

### Control Server
Control server has a client interface that enables the user to call Control Server's commands from the terminal in
the same way the Subordinate Server does, for example, when spamming `SYN`. 

# How to launch

## Control server

``` java -jar rpi-broadcasting-server.jar <type> <listening_port> <subordinate_listening_port> <broadcast_address> <localhost IPv4 address>```

## Subordinate server

All subordinate servers must listen on the same port.

``` java -jar rpi-broadcasting-server.jar <type> <listening_port> <control_server_listening_port> <broadcast_address> <localhost IPv4 address>```
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
* ``3 - Maciek's team robot  ``


## Command format (communication between user and Control server)

The format of the input command.

``[target_device_type] + [cmd]``

### Command format (communication between servers)

This is the format of the command being exchanged over the network between **Control Server** and **Subordinate Servers**.

>``[sender_type] + [cmd] + ((cmd == ACK)? [cmd2]) + [sender_ip]``
>  
> Please note that:  
>* **cmd** is always 3-character long   
>* if **cmd** is ``ACK`` then **cmd2** is the command that was sent to the sender  

## Protocol 

### Shared

``END``: terminates launcher

### Control Server 

`SYN`: creates new entry for the sender  
`DEL`: deletes entry of the sender

### Subordinate server

`ACKSYN` stores the IP of the sender as **Control Server**

# How to launch

## Control server

``` java -jar rpi-broadcasting-server.jar 1 <listening_port> <subordinate_listening_port> <broadcast_address> <localhost IPv4 address>```

## Subordinate server

All subordinate servers must listen on the same port.

``` java -jar rpi-broadcasting-server.jar 0 <listening_port> <control_server_listening_port> <broadcast_address> <localhost IPv4 address>```
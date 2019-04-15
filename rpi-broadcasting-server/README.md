
# To Control Server

**END**: shuts down control server

**SYNXXX.XXX.XXX.XXX**: answer to SYN... ; put your local IPv4 address in the packet

**DELXXX.XXX.XXX.XXX**: removes ip entry after DEL from control server

# From Control Server

**SYNXXX.XXX.XXX.XXX**: must contain sender's local IP address, produces a SYN... response

# How to launch

## Control server

``` java -jar rpi-broadcasting-server.jar 1 <listening_port> <subordinate_listening_port> <broadcast_address> <localhost IPv4 address>```

## Subordinate server

All subordinate servers must listen on the same port.

``` java -jar rpi-broadcasting-server.jar 0 <listening_port> <control_server_listening_port> <broadcast_address> <localhost IPv4 address>```
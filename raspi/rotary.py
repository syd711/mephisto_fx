import gaugette.rotary_encoder
import gaugette.switch
import socket

A_PIN  = 1
B_PIN  = 4

HOST = 'localhost'       # Symbolic name meaning the local host
PORT = 2106              # Arbitrary non-privileged port

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect((HOST, PORT))

encoder = gaugette.rotary_encoder.RotaryEncoder(A_PIN, B_PIN)

last_state = None
count = 0

while True:
    delta = encoder.get_delta()
    if delta!=0:
       count = count+delta
       if count>=4:
            #print "rotate left"
            s.send('-1#')
            count = 0
       if count<=-4:
            #print "rotate right"
            s.send('1#')
            count = 0

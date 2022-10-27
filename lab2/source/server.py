#!/usr/bin/env python3
import http.server
import socketserver
import os
import time

#print('source code for "http.server":', http.server.__file__)

class web_server(http.server.SimpleHTTPRequestHandler):
    
    def do_GET(self):

        print(self.path)
        
        if self.path == '/':
            self.protocol_version = 'HTTP/1.1'
            self.send_response(200)
            self.send_header("Content-type", "text/html; charset=UTF-8")
            self.end_headers()
            t = time.localtime()
            timeS = time.strftime("%H:%M:%S", t)
            self.wfile.write(b"Hello World!\n")
            self.wfile.write(b"\n" + timeS.encode("utf-8"))
        elif self.path.startswith('/?cmd='):
            parameter = self.path[6:]
            self.protocol_version = 'HTTP/1.1'
            self.send_response(200)
            self.send_header("Content-type", "text/html; charset=UTF-8")
            self.end_headers()
            self.wfile.write(parameter.encode("utf-8"))
        else:
            super().do_GET()
    
# --- main ---

PORT = 4080

print(f'Starting: http://localhost:{PORT}')

tcp_server = socketserver.TCPServer(("",PORT), web_server)
tcp_server.serve_forever()

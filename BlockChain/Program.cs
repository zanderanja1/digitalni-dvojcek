using System;  
using System.Collections.Generic;  
using System.Net;  
using System.Net.Sockets;  
using System.IO;  
using System.Text;  
using System.Threading;  
using System.Security.Cryptography;
using System.Text.Json;
using System.Text.Json.Serialization;
 
namespace Server  
{  
    public struct Global
    {
        public static int counter = 0;
    }
    public class Block  
    {  
        public int Index; 
        public DateTime TimeStamp; 
        public string? PreviousHash; 
        public string Hash;
        public string Data; 
        public int Nonce = 0;
    
        public Block(DateTime timeStamp, string? previousHash, string data)  
        {  
            Index = Global.counter;  
            TimeStamp = timeStamp;  

            if(Global.counter == 0)
                PreviousHash = "0";  
            else
                PreviousHash = previousHash;  

            Global.counter++;
            Data = data;  
            Hash = ZracunajHash(1); 
        }  
        
        public string ZracunajHash(int Diff)  
        {  
            SHA256 sha256 = SHA256.Create();  
            byte[] inputBytes;

            if(PreviousHash == null)
                inputBytes = Encoding.ASCII.GetBytes($"{Index}+{TimeStamp}+{Data}+{""}+{Diff}+{Nonce}");  
            else 
                inputBytes = Encoding.ASCII.GetBytes($"{Index}+{TimeStamp}+{Data}+{PreviousHash}+{Diff}+{Nonce}");  
            
            byte[] outputBytes = sha256.ComputeHash(inputBytes);  
        
            return Convert.ToBase64String(outputBytes);  
        }  
        public void Rudarjenje(int tezavnost)  
        {  
            var nicle = new string('0', tezavnost);  
            
            while (this.Hash == null || this.Hash.Substring(0, tezavnost) != nicle)  
            {  
                this.Nonce++;  
                this.Hash = this.ZracunajHash(tezavnost);  
            }  
        }  
    }  
    public class Blockchain  
    {  
        public IList<Block> Chain { set;  get; }  
        public int Difficulty { set; get; } = 3;

        public Blockchain()  
        {    
            Chain = new List<Block>();   
        } 
        
        public Block? GetLatestBlock()  
        {  
            if(Chain.Count >= 1)
                return Chain[Chain.Count - 1];
            else 
                return null;  
        }  
    
        public void DodajBlock(Block block)  
        {  
            Block? zadnjiBlock = GetLatestBlock();  

            if(zadnjiBlock != null)
            {
                block.Index = zadnjiBlock.Index + 1;  
                block.PreviousHash = zadnjiBlock.Hash;  
            }
            else
            {
                block.Index = 1;  
                block.PreviousHash = "0";  
            }

            block.Rudarjenje(this.Difficulty);  
            Chain.Add(block);  
        } 
        public bool IsValid()  
        {  
            for (int i = 1; i < Chain.Count; i++)  
            {  
                Block trenutniBlock = Chain[i];  
                Block prejsnjiBlock = Chain[i - 1];  
        
                if (trenutniBlock.Hash != trenutniBlock.ZracunajHash(this.Difficulty))  
                {  
                    return false;  
                }  
        
                if (trenutniBlock.PreviousHash != prejsnjiBlock.Hash)  
                {  
                    return false;  
                }  
            }  
            return true;  
        }  
        private static TcpListener? tcpListener;  
        private static List<TcpClient> tcpClientsList = new List<TcpClient>();  
 
        public static string? jsonString;


        static void Main(string[] args)  
        {  
            
            var startTime = DateTime.Now;  

            Blockchain verigaBlokov = new Blockchain();  
            verigaBlokov.DodajBlock(new Block(DateTime.Now, null, "podatek 1"));  
            verigaBlokov.DodajBlock(new Block(DateTime.Now, null, "podatek 2"));  
            verigaBlokov.DodajBlock(new Block(DateTime.Now, null, "podatek 3"));  
            
            var endTime = DateTime.Now;  

            jsonString = JsonSerializer.Serialize(verigaBlokov);

            //Csonsole.WriteLine($"Duration: {endTime - startTime}"); 
            char? selectInput = Console.ReadLine()[0];
            if(selectInput == 'S')
            { //server 
                tcpListener = new TcpListener(IPAddress.Any, 1234);  
                tcpListener.Start();  
    
                //Console.WriteLine("Server started");  
    
                while (true)  
                {  
                    TcpClient tcpClient = tcpListener.AcceptTcpClient();  
                    tcpClientsList.Add(tcpClient);  
    
                    Thread thread = new Thread(Client);  
                    thread.Start(tcpClient);  
                }  
            }
            else
            {
                 try  
                {  
                    TcpClient tcpClient = new TcpClient("127.0.0.1", 1234);  
                    //Console.WriteLine("Connected to server.");  
                    Console.WriteLine("");  
    
                    Thread thread = new Thread(Read);  
                    thread.Start(tcpClient);  
    
                    StreamWriter sWriter = new StreamWriter(tcpClient.GetStream());  
    
                    while (true)  
                    {  
                        if (tcpClient.Connected)  
                        {  
                            string input = Console.ReadLine();  
                            sWriter.WriteLine(input);  
                            sWriter.Flush();  
                        }  
                    }  
    
                }  
                catch (Exception e)  
                {  
                    Console.Write(e.Message);  
                }  
    
                //Console.ReadKey();  
                }
        }  
        static void Read(object obj)  
        {  
            TcpClient tcpClient = (TcpClient)obj;  
            StreamReader sReader = new StreamReader(tcpClient.GetStream());  
 
            while (true)  
            {  
                try  
                {  
                    string message = sReader.ReadLine();  
                    Console.WriteLine(message);  
                }  
                catch (Exception e)  
                {  
                    Console.WriteLine(e.Message);  
                    break;  
                }  
            }  
        }  
        public static void Client(object obj)  
        {  
            TcpClient tcpClient = (TcpClient)obj;  
            //StreamReader reader = new StreamReader(tcpClient.GetStream());  
 
            Console.WriteLine("Client connected");  
 
            while (true)  
            {  
                Console.ReadLine();
                BroadCast(jsonString);  
                Console.WriteLine(jsonString);  
            }  
        }  
 
        public static void BroadCast(string msg)  
        {  
            foreach (TcpClient client in tcpClientsList)  
            {  
                StreamWriter sWriter = new StreamWriter(client.GetStream());  
                sWriter.WriteLine(msg);  
                sWriter.Flush();  
            }  
        }  
    }  
} 
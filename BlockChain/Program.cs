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
using System.Runtime.Intrinsics.Arm;



namespace Server  
{  
    public struct Global
    {
        public static int threads = 1;
        public static int counter = 0;
        public static int generated = 0;
        public static int diffAdjustInterval = 10;
        public static int blockGenerationInterval = 10000;
    }
    public class Block  
    {  
        public int Index; 
        public DateTime TimeStamp; 
        public string? PreviousHash; 
        public string Hash;
        public string Data; 
        public int Nonce = 0;

        public int Difficulity;
    
        public Block(DateTime timeStamp, string? previousHash, string data, int difficulity)  
        {  
            Index = Global.counter;  
            TimeStamp = timeStamp;  

            if(previousHash != null)
                PreviousHash = previousHash;  
            else
                PreviousHash = "";  

            Data = data;  
            Difficulity = difficulity;
            Hash = Rudarjenje(Global.threads);
            
        }  
        
        public string ZracunajHash()  
        {  
            SHA256 sha256 = SHA256.Create();  
            byte[] inputBytes;

            inputBytes = Encoding.ASCII.GetBytes($"{Index}+{TimeStamp}+{Data}+{PreviousHash}+{Difficulity}+{Nonce}");  
            
            byte[] outputBytes = sha256.ComputeHash(inputBytes);  
        
            return Convert.ToBase64String(outputBytes);  
        }

   
        public string Rudarjenje(int numberOfThreads)
        {
            var nicle = new string('0', Difficulity);
            Thread[] threads = new Thread[numberOfThreads];
            object lockObject = new object();
            string validHash = "";

            for (int i = 0; i < numberOfThreads; i++)
            {
                threads[i] = new Thread(() =>
                {
                    while (true)
                    {
                        string localHash;
                        int localNonce;

                        lock (lockObject)
                        {
                            if (validHash != "" && validHash.Substring(0, Difficulity) == nicle)
                            {
                                break;
                            }

                            this.Nonce++;
                            localHash = this.ZracunajHash();
                            localNonce = this.Nonce;
                        

                        if (localHash.Substring(0, Difficulity) == nicle)
                        {
                            lock (lockObject)
                            {
                                if (validHash == "")
                                {
                                    validHash = localHash;
                                }
                            }
                            break;
                        }
                        }
                    }
                });
                threads[i].Start();
            }

            for (int i = 0; i < numberOfThreads; i++)
            {
                threads[i].Join();
            }

            return validHash;
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
            Chain.Add(block);    
        } 

         public Boolean validateBlock(Block block) {
            Block? lastBlock = GetLatestBlock();

            if(lastBlock != null)
            {
                String newHash = block.ZracunajHash();
                if(lastBlock.Index + 1 == block.Index && lastBlock.Hash == block.PreviousHash && newHash == block.Hash)
                {
                    return true;
                }
                else 
                {
                    return false;
                }
            }
            else {
                return true;
            }
         }
        public bool IsValid()  
        {  
            for (int i = 1; i < Chain.Count; i++)  
            {  
                Block trenutniBlock = Chain[i];  
                Block prejsnjiBlock = Chain[i - 1];  
        
                if (trenutniBlock.Hash != trenutniBlock.ZracunajHash())  
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

        public int adjustDiff() {
            var previousAdjustmentBlock = Chain[Chain.Count - Global.diffAdjustInterval];
            var timeExpected = Global.blockGenerationInterval * Global.diffAdjustInterval;
            var timeTaken = this.GetLatestBlock().TimeStamp - previousAdjustmentBlock.TimeStamp;
            Console.WriteLine("Time expected: " + timeExpected);
            Console.WriteLine("Time taken: " + timeTaken);
            if (timeTaken.TotalMilliseconds < (timeExpected / 2))
            {
                return previousAdjustmentBlock.Difficulity + 1;
            }
            else if(timeTaken.TotalMilliseconds > (timeExpected * 2))
            {
                return previousAdjustmentBlock.Difficulity;
            }
            else{
                return previousAdjustmentBlock.Difficulity;
            }
               
        }
        private static TcpListener? tcpListener;  
        private static List<TcpClient> tcpClientsList = new List<TcpClient>();  
 
        public static string? jsonString;

        public void generateAndValidateBlock(Blockchain blockchain) {
            Block latest = blockchain.GetLatestBlock();
            String? prevHash = null;
            if(latest != null) {
                prevHash = latest.Hash;
            }
            Block block = new Block(DateTime.Now, prevHash, "podatek " + Global.counter, blockchain.Difficulty);
            if(blockchain.validateBlock(block)) {
                Global.counter++;
                Global.generated++;
                blockchain.DodajBlock(block);
                Console.WriteLine("Added new block: " + block.Difficulity);
                if(Global.generated == Global.diffAdjustInterval)
                {
                    Console.WriteLine("Adjusting difficulity");
                    Difficulty = adjustDiff();
                    Console.WriteLine("New difficulity: " + Difficulty);
                    Global.generated = 0;
                }
            } else {
                Console.WriteLine("Block not valid");
            }
        }


        static void Main(string[] args)  
        {  
             

            Blockchain verigaBlokov = new Blockchain();
            var startTime = DateTime.Now;
            for(int i = 0; i < 22; i++) {
                verigaBlokov.generateAndValidateBlock(verigaBlokov);
            }
            

            var endTime = DateTime.Now; 
            Console.WriteLine($"Duration: {endTime - startTime}"); 
            for(int i = 0; i < verigaBlokov.Chain.Count; i++) {
                Console.WriteLine(verigaBlokov.Chain[i].Hash + "  " + verigaBlokov.Chain[i].PreviousHash);
            }

            jsonString = JsonSerializer.Serialize(verigaBlokov);

        }  
        
    }  
} 
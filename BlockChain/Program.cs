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
using System.Runtime.CompilerServices;
using MPI;



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
        public int Nonce;
        public int Difficulity;
    
        public Block(int index, DateTime timeStamp, string previousHash, string hash, string data, int nonce, int difficulity)  
        {  
            Index = index; 
            TimeStamp = timeStamp;
            if(previousHash == null) {
                PreviousHash = "";
            } else {
                PreviousHash = previousHash;
            }
            Hash = hash;
            Data = data;
            Nonce = nonce;
            Difficulity = difficulity;
           
            
        }  
        
        public string CalculateHash()  
        {  
            SHA256 sha256 = SHA256.Create();  
            byte[] inputBytes;

            inputBytes = Encoding.ASCII.GetBytes($"{Index}+{TimeStamp}+{Data}+{PreviousHash}+{Difficulity}+{Nonce}");  
            
            byte[] outputBytes = sha256.ComputeHash(inputBytes);  
        
            return Convert.ToBase64String(outputBytes);  
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
    
        public void AddBlock(Block block)  
        {  
            Chain.Add(block);    
        } 

         public Boolean ValidateBlock(Block block) {
            Block? lastBlock = GetLatestBlock();

            if(lastBlock != null)
            {
                String newHash = block.CalculateHash();
                if(lastBlock.Index + 1 == block.Index && lastBlock.Hash == block.PreviousHash && newHash == block.Hash && block.TimeStamp  <= DateTime.Now.AddMilliseconds(60000))
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
        
                if (trenutniBlock.Hash != trenutniBlock.CalculateHash())  
                {  
                    return false;  
                }  
        
                if (trenutniBlock.PreviousHash != prejsnjiBlock.Hash)  
                {  
                    return false;  
                } 

                if(trenutniBlock.TimeStamp < prejsnjiBlock.TimeStamp.AddMilliseconds(-60000))
                {
                    return false;
                }
            }  
            return true;  
        }

        

        public string CalculateHash(int Index, DateTime TimeStamp, String Data, String? PreviousHash, int Difficulity, int Nonce)  
        {  
            SHA256 sha256 = SHA256.Create();  
            byte[] inputBytes;

            inputBytes = Encoding.ASCII.GetBytes($"{Index}+{TimeStamp}+{Data}+{PreviousHash}+{Difficulity}+{Nonce}");  
            
            byte[] outputBytes = sha256.ComputeHash(inputBytes);  
        
            return Convert.ToBase64String(outputBytes);  
        }


        public Block Mine(int index, DateTime timeStamp, string Data, string? PreviousHash, int Difficulity)
        {
            int numberOfThreads = 1;
            Thread[] threads = new Thread[numberOfThreads];
            object lockObject = new object();
            string validHash = "";
            int validNonce = 0;

            for (int i = 0; i < numberOfThreads; i++)
            {
                threads[i] = new Thread(() =>
                {
                    int localNonce = 0;
                    string localHash;
                    var nicle = new string('0', Difficulity);

                    while (true)
                    {
                        // Move the hash calculation outside the lock
                        localNonce++;
                        localHash = CalculateHash(index, timeStamp, Data, PreviousHash, Difficulity, localNonce);

                        lock (lockObject)
                        {
                            if (validHash != "" && validHash.Substring(0, Difficulity) == nicle)
                            {
                                break;
                            }

                            if (localHash.Substring(0, Difficulity) == nicle)
                            {
                                if (validHash == "")
                                {
                                    validHash = localHash;
                                    validNonce = localNonce;
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

            return new Block(index, timeStamp, PreviousHash, validHash, Data, validNonce, Difficulity);
        }

        public int CalculateCumulativeDifficulty(Blockchain chain)
        {
            int cumulativeDifficulty = 0;
            foreach (Block block in chain.Chain)
            {
                cumulativeDifficulty += (int)Math.Pow(2, block.Difficulity);
            }
            return cumulativeDifficulty;
        }

        
    }

    class Program
    {

        public static int AdjustDiff(Blockchain blockchain) {
            var previousAdjustmentBlock = blockchain.Chain[blockchain.Chain.Count - Global.diffAdjustInterval];
            var timeExpected = Global.blockGenerationInterval * Global.diffAdjustInterval;
            var timeTaken = blockchain.GetLatestBlock().TimeStamp - previousAdjustmentBlock.TimeStamp;
            Console.WriteLine("Time expected: " + timeExpected);
            Console.WriteLine("Time taken: " + timeTaken.TotalMilliseconds);
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

        public static void generateAndValidateBlock(Blockchain blockchain) {
            Block latest = blockchain.GetLatestBlock();
            string? prevHash = null;
            if(latest != null) {
                prevHash = latest.Hash;
            }
            Block block = blockchain.Mine(Global.counter, DateTime.Now, "podatek " + Global.counter, prevHash, blockchain.Difficulty);
            if(blockchain.ValidateBlock(block)) {
                Global.counter++;
                Global.generated++;
                blockchain.AddBlock(block);
                Console.WriteLine("Added new block: " + block.Difficulity);
                if(Global.generated == Global.diffAdjustInterval)
                {
                    Console.WriteLine("Adjusting difficulity");
                    blockchain.Difficulty = AdjustDiff(blockchain);
                    Console.WriteLine("New difficulity: " + blockchain.Difficulty);
                    Global.generated = 0;
                }
            } else {
                Console.WriteLine("Block not valid");
            }
        }
        static void Main(string[] args)
        {
            /* Blockchain verigaBlokov = new Blockchain();
            var startTime = DateTime.Now;
            for(int i = 0; i < 22; i++) {
                generateAndValidateBlock(verigaBlokov);
            }
            

            var endTime = DateTime.Now; 
            Console.WriteLine($"Duration: {endTime - startTime}"); 
            for(int i = 0; i < verigaBlokov.Chain.Count; i++) {
                Console.WriteLine(verigaBlokov.Chain[i].Hash + "  " + verigaBlokov.Chain[i].PreviousHash);
            }

            bool valid = verigaBlokov.IsValid();
            Console.WriteLine(valid ? "Chain valid" : "Chain not valid"); */

            MPI.Environment.Run(ref args, communicator =>
            {
                Console.WriteLine("Hello, from process number "
                                        + communicator.Rank + " of "
                                        + communicator.Size);
            });
        }

        
    }

    
} 
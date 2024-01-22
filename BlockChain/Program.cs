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
using System.Data;
using System.Diagnostics;



namespace Server  
{  
    public struct Global
    {
        public static int threads = 1;
        public static int hashes = 0;
        public static int counter = 0;
        public static int generated = 0;
        public static int diffAdjustInterval = 10;
        public static int blockGenerationInterval = 5000;
        public static int syncInterval = 20000;
    }

    [Serializable]
    public class Block  
    {  
        public int Index; 
        public DateTime TimeStamp; 
        public string? PreviousHash; 
        public string Hash;
        public string Data; 
        public int Nonce;
        public int Difficulty;
    
        public Block(int index, DateTime timeStamp, string previousHash, string hash, string data, int nonce, int difficulty)  
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
            Difficulty = difficulty;
           
            
        }  
        
        public string CalculateHash()  
        {  
            SHA256 sha256 = SHA256.Create();  
            byte[] inputBytes;

            inputBytes = Encoding.ASCII.GetBytes($"{Index}+{TimeStamp}+{Data}+{PreviousHash}+{Difficulty}+{Nonce}");  
            
            byte[] outputBytes = sha256.ComputeHash(inputBytes);  
        
            return Convert.ToBase64String(outputBytes);  
        }
       
    }  

    [Serializable]
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

        public int GetLength()
        {
            return Chain.Count;
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

         public int GetNonces() {
            int sum = 0;
            for(int i = 0; i < Chain.Count; i++) {
                sum += Chain[i].Nonce;
            }
            return sum;
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

        

        public string CalculateHash(int Index, DateTime TimeStamp, String Data, String? PreviousHash, int Difficulty, int Nonce)  
        {  
            SHA256 sha256 = SHA256.Create();  
            byte[] inputBytes;

            inputBytes = Encoding.ASCII.GetBytes($"{Index}+{TimeStamp}+{Data}+{PreviousHash}+{Difficulty}+{Nonce}");  
            
            byte[] outputBytes = sha256.ComputeHash(inputBytes);  
        
            return Convert.ToBase64String(outputBytes);  
        }


        /* public Block Mine(int index, DateTime timeStamp, string Data, string? PreviousHash, int Difficulty)
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
                    var nicle = new string('0', Difficulty);

                    while (true)
                    {
                        // Move the hash calculation outside the lock
                        localNonce++;
                        localHash = CalculateHash(index, timeStamp, Data, PreviousHash, Difficulty, localNonce);

                        lock (lockObject)
                        {
                            if (validHash != "" && validHash.Substring(0, Difficulty) == nicle)
                            {
                                break;
                            }

                            if (localHash.Substring(0, Difficulty) == nicle)
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

            return new Block(index, timeStamp, PreviousHash, validHash, Data, validNonce, Difficulty);
        } */

        public Block Mine(int index, DateTime timeStamp, string Data, string? PreviousHash, int Difficulty, int numThreads)
        {
            Thread[] threads = new Thread[numThreads];
            object lockObject = new object();
            object lockObject2 = new object();
            string validHash = "";
            int validNonce = 0;
            int nonceRange = 100000;
            var nicle = new string('0', Difficulty);
            bool solutionFound = false;
            int nonceCount = 0;
            

            for (int i = 0; i < numThreads; i++)
            {
                int localI = i;
                threads[i] = new Thread(() =>
                {
                    string localHash;
                    while (true)
                    {
                        var localNonce = 0;
                        lock (lockObject2)
                        {
                            localNonce = nonceCount * nonceRange;
                            nonceCount++;
                        }
                        if (solutionFound)
                        {
                            break;
                        }
                        for (int j = 0; j < nonceRange; j++)
                        {
                            if (solutionFound)
                            {
                                break;
                            }
                            localNonce++;
                            localHash = CalculateHash(index, timeStamp, Data, PreviousHash, Difficulty, localNonce);
                            
                            if (localHash.Substring(0, Difficulty) == nicle)
                            {
                                lock (lockObject)
                                {
                                    if (!solutionFound && (validHash == "" || validHash.Substring(0, Difficulty) != nicle))
                                    {
                                        validHash = localHash;
                                        validNonce = localNonce;
                                        solutionFound = true;
                                    }
                                }
                                break;
                            }
                        }
                    }
                });
                threads[i].Start();
            }

            for (int i = 0; i < numThreads; i++)
            {
                threads[i].Join();
            }

            return new Block(index, timeStamp, PreviousHash, validHash, Data, validNonce, Difficulty);
        }

        /* public Block Mine(int index, DateTime timeStamp, string Data, string? PreviousHash, int Difficulty, int numThreads)
        {
            Thread[] threads = new Thread[numThreads];
            object lockObject = new object();
            string validHash = "";
            int validNonce = 0;
            int nonceRange = 100000;
            var nicle = new string('0', Difficulty);
            bool solutionFound = false;


            for (int i = 0; i < numThreads; i++)
            {
                int startNonce = i * nonceRange;
                int localI = i;
                threads[i] = new Thread(() =>
                {
                    int localNonce = startNonce;
                    string localHash;
                    //Console.WriteLine("Thread " + localI +" started");

                    while (true)
                    {
                        if (solutionFound)
                        {
                            return;
                        }
                        //Console.WriteLine("Thread " + localI + " start " + startNonce);
                        for (int j = 0; j < nonceRange; j++)
                        {
                            if (solutionFound)
                            {
                                return;
                            }
                            localNonce++;
                            localHash = CalculateHash(index, timeStamp, Data, PreviousHash, Difficulty, localNonce);
                            
                            if (localHash.Substring(0, Difficulty) == nicle)
                            {
                                lock (lockObject)
                                {
                                    if (!solutionFound && (validHash == "" || validHash.Substring(0, Difficulty) != nicle))
                                    {
                                        //Console.WriteLine("Found solution on thread " + localI);
                                        validHash = localHash;
                                        validNonce = localNonce;
                                        solutionFound = true;
                                    }
                                }
                                return;
                            }
                        }
                        startNonce += nonceRange * numThreads;
                        localNonce = startNonce;
                    }
                });
                threads[i].Start();
            }

            for (int i = 0; i < numThreads; i++)
            {
                threads[i].Join();
            }

            return new Block(index, timeStamp, PreviousHash, validHash, Data, validNonce, Difficulty);
        } */



        public int CalculateCumulativeDifficulty()
        {
            int cumulativeDifficulty = 0;
            foreach (Block block in Chain)
            {
                cumulativeDifficulty += (int)Math.Pow(2, block.Difficulty);
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
            //Console.WriteLine("Time expected: " + timeExpected);
            //Console.WriteLine("Time taken: " + timeTaken.TotalMilliseconds);
            if (timeTaken.TotalMilliseconds < (timeExpected / 2))
            {
                return previousAdjustmentBlock.Difficulty + 1;
            }
            else if(timeTaken.TotalMilliseconds > (timeExpected * 2))
            {
                return previousAdjustmentBlock.Difficulty - 1;
            }
            else{
                return previousAdjustmentBlock.Difficulty;
            }
               
        }

        public static void generateAndValidateBlock(Blockchain blockchain, Intracommunicator communicator, int rank, int numThreads) 
        {
            Block latest = blockchain.GetLatestBlock();
            string? prevHash = null;
            if(latest != null) 
            {
                prevHash = latest.Hash;
            }
            Block block = blockchain.Mine(Global.counter, DateTime.Now, "podatek " + Global.counter, prevHash, blockchain.Difficulty, numThreads);
            if(blockchain.ValidateBlock(block)) 
            {
                Global.counter++;
                Global.generated++;
                blockchain.AddBlock(block);
                //Console.WriteLine("Added new block: " + block.Difficulty + ", on node " + rank + " with nonce " + block.Nonce);
                if(Global.generated == Global.diffAdjustInterval)
                {
                    //Console.WriteLine("Adjusting Difficulty");
                    blockchain.Difficulty = AdjustDiff(blockchain);
                    //Console.WriteLine("New Difficulty: " + blockchain.Difficulty);
                    Global.generated = 0;
                }

                // If 10 blocks have been mined, send the chain to the central node
                if (blockchain.GetLength() % 10 == 0)
                {
                    communicator.Send(blockchain, 0, 0);
                }
            } 
            else 
            {
                //Console.WriteLine("Block not valid on node " + rank);
            }
        }

      

        static void Main(string[] args)
        {
            int numThreads = 1; // Default value

            // Check if any arguments were passed
            for (int i = 0; i < args.Length; i++)
            {
                if (args[i] == "-numThreads" && i + 1 < args.Length)
                {
                    // Parse the next argument as an integer
                    if (int.TryParse(args[i + 1], out numThreads))
                    {
                        //Console.WriteLine("Number of threads: " + numThreads);
                    }
                    else
                    {
                        //Console.WriteLine("Invalid number of threads. Please enter a valid integer.");
                    }
                }
            }
            MPI.Environment.Run(ref args, communicator =>
            {
                Blockchain verigaBlokov = new Blockchain();
                Stopwatch stopwatch = new Stopwatch();

                stopwatch.Start();

                while(true)
                {
                    if (communicator.Rank != 0) // All nodes except the central node mine blocks
                    {
                        // Check for stop signal from central node
                        if (communicator.ImmediateProbe(0, 1) != null)
                        {
                            var stopSignal = communicator.Receive<bool>(0, 1);
                            if (stopSignal)
                            {
                                // Send confirmation message back to central node
                                communicator.Send(true, 0, 1);

                                // Send data to central node
                                var data = verigaBlokov.GetNonces();
                                communicator.Send(data, 0, 2);
                                break;
                            }
                        }

                        generateAndValidateBlock(verigaBlokov,communicator, communicator.Rank, numThreads);
                    }

                    if (communicator.Rank == 0) // The central node listens for incoming chains and handles them
                    {
                        if (verigaBlokov.GetLength() >= 20)
                        {
                            stopwatch.Stop();
                            

                            for (int i = 1; i < communicator.Size; i++)
                            {
                                communicator.Send(true, i, 1);
                            }

                            // Wait for all nodes to send back a confirmation message and receive data
                            for (int i = 1; i < communicator.Size; i++)
                            {
                                communicator.Receive<bool>(i, 1);

                                // Receive data from node
                                var data = communicator.Receive<int>(i, 2); // Replace DataType with the type of your data
                                Global.hashes += data;

                                // Handle received data here
                            }

                            Console.WriteLine("Time taken: " + stopwatch.Elapsed.TotalSeconds);
                            Console.WriteLine("Cumulative difficulty " + verigaBlokov.CalculateCumulativeDifficulty());
                            Console.WriteLine("Hashes " + Global.hashes);
                            double speed = Global.hashes/stopwatch.Elapsed.TotalSeconds;
                            Console.WriteLine("Speed " + speed);

                            break;
                        }

                        if (communicator.ImmediateProbe(Intracommunicator.anySource, Intracommunicator.anyTag) != null)
                        {
                            var receiveRequest = communicator.ImmediateReceive<Blockchain>(Intracommunicator.anySource, Intracommunicator.anyTag);
                            var status = receiveRequest.Wait();
                            var receivedChain = (Blockchain)receiveRequest.GetValue();
                            //Console.WriteLine("Received chain on central node from node " + status.Source);
                            
                            verigaBlokov = HandleReceivedChain(receivedChain, verigaBlokov, status.Source);
                        }
                    }
                }
            });
        }

        static Blockchain HandleReceivedChain(Blockchain received, Blockchain current, int rank)
        {
            int currentCumuluativeDiff = current.CalculateCumulativeDifficulty();
            int receivedCumulativeDiff = received.CalculateCumulativeDifficulty();

            //Console.WriteLine("My difficulty " + currentCumuluativeDiff);
            //Console.WriteLine("Node " + rank + " difficulty " + receivedCumulativeDiff);

            if(receivedCumulativeDiff > currentCumuluativeDiff)
            {
                //Console.WriteLine("Rewriting my blockchain with " + rank);
                return received;
            }
            else
            {
                //Console.WriteLine("Keeping the current chain");
                return current;
            }

        }

    }

    

} 
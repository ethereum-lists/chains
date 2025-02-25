import asyncio
import json
import httpx
import pymongo
import os

# إعداد MongoDB
MONGO_URI = os.getenv("MONGO_URI", "mongodb://localhost:27017/")
DB_NAME = "evm_chains"
COLLECTION_NAME = "chains"

# رابط API الأساسي
CHAIN_DATA_URL = "https://chainid.network/chains.json"

async def fetch_chains():
    """ جلب بيانات الشبكات من ChainID """
    async with httpx.AsyncClient() as client:
        response = await client.get(CHAIN_DATA_URL)
        if response.status_code == 200:
            return response.json()
        return []

async def validate_data(chains):
    """ التحقق من صحة البيانات وتنظيف الأخطاء """
    unique_ids = set()
    for chain in chains:
        if chain["chainId"] in unique_ids:
            print(f"⚠️ تحذير: معرف السلسلة مكرر {chain['chainId']}")
        unique_ids.add(chain["chainId"])
    return chains

async def save_to_file(chains):
    """ حفظ البيانات في ملف JSON """
    with open("chains.json", "w", encoding="utf-8") as f:
        json.dump(chains, f, indent=2, ensure_ascii=False)
    print("✅ تم حفظ البيانات في chains.json")

async def save_to_mongodb(chains):
    """ تخزين البيانات في MongoDB """
    client = pymongo.MongoClient(MONGO_URI)
    db = client[DB_NAME]
    collection = db[COLLECTION_NAME]
    collection.delete_many({})
    collection.insert_many(chains)
    print("✅ تم تخزين البيانات في MongoDB")

async def main():
    chains = await fetch_chains()
    if chains:
        chains = await validate_data(chains)
        await save_to_file(chains)
        await save_to_mongodb(chains)

if __name__ == "__main__":
    asyncio.run(main())

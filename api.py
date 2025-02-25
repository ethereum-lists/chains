from flask import Flask, jsonify
import pymongo
import os

app = Flask(__name__)

# إعداد MongoDB
MONGO_URI = os.getenv("MONGO_URI", "mongodb://localhost:27017/")
DB_NAME = "evm_chains"
COLLECTION_NAME = "chains"

client = pymongo.MongoClient(MONGO_URI)
db = client[DB_NAME]
collection = db[COLLECTION_NAME]

@app.route('/chains', methods=['GET'])
def get_chains():
    """ إرجاع قائمة الشبكات من قاعدة البيانات """
    chains = list(collection.find({}, {"_id": 0}))  # استبعاد حقل MongoDB الافتراضي
    return jsonify(chains)

@app.route('/chain/<int:chain_id>', methods=['GET'])
def get_chain(chain_id):
    """ البحث عن شبكة باستخدام Chain ID """
    chain = collection.find_one({"chainId": chain_id}, {"_id": 0})
    return jsonify(chain) if chain else jsonify({"error": "Chain not found"}), 404

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)

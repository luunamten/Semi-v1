import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import {C} from './C';
import {EarthQuadGrid} from './EarthQuadGrid';
import {LatLng} from './LatLng';
import * as utils from './utils';
admin.initializeApp();
// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

/** input:
*data.from
*data.centerLat
*data.centerLng
*data.storeType
*/
export const nearbyStores = functions.https.onCall((data, context) => {
    const grid: EarthQuadGrid = EarthQuadGrid.instance;
    const from: number = data.from;
    const type: number = data.storeType;
    const center: LatLng = new LatLng(data.centerLat, data.centerLng);
    const dimen: number = utils.kmToLat(C.NEARBY_DIMEN);
    const rects: Set<number> = grid.searchCollisionBoxes(center, dimen);
    const limit: number = Math.pow(4, EarthQuadGrid.MAX_LEVELS - grid.getCollisionLevel(dimen));
    const halfDimen: number = dimen / 2;
    const topLeft: LatLng = new LatLng(center.latitude + halfDimen, center.longitude - halfDimen);
    const bottomRight: LatLng = new LatLng(center.latitude - halfDimen, center.longitude + halfDimen);
    const promises: Promise<FirebaseFirestore.QuerySnapshot>[] = [];
    for(const rect of rects) {
        const limitRect: number = rect + limit;
        let colRef: FirebaseFirestore.Query = admin.firestore().collection('store')
                        .select('title', 'address', 'imageURL', 'rating', 'geo')
                        .where('gridNumber', '>=', rect)
                        .where('gridNumber', '<', limitRect);
        if(type !== undefined && type >= 0) {
            colRef = colRef.where('type', '==', type);
        }
        promises.push(colRef.get());
    }
    return Promise.all(promises).then(snapshots => {
        const stores: Object[] = [];
        const distance2s: number[] = [];
        snapshots.forEach(snapshot => {
            snapshot.forEach(store => {
                const storeData: FirebaseFirestore.DocumentData = store.data();
                const location: LatLng = new LatLng(storeData.geo.latitude, 
                    storeData.geo.longitude);
                if(utils.isObjectInRect(topLeft, bottomRight, location)) {
                    const newdata: Object = {
                        id : store.id,
                        title : storeData.title,
                        address : storeData.address,
                        imageURL : storeData.imageURL,
                        rating : storeData.rating,
                        geo : storeData.geo
                    }
                    distance2s.push(utils.distance2(location, center));
                    stores.push(newdata);
                    utils.sortAsc(distance2s, stores);
                }
            });
        });
        return stores.slice(from, from + C.NUM_NEARBY_STORES);
    }).catch(err => {
        console.log(err);
        return [];
    });
});

/** input:
*data.from
*data.centerLat
*data.centerLng
*data.storeType
*data.dimen
*data.keywords
*/
export const nearbyStoresByKeywords = functions.https.onCall((data, context) => {
    const grid: EarthQuadGrid = EarthQuadGrid.instance;
    const from: number = data.from;
    const type: number = data.storeType;
    const center: LatLng = new LatLng(data.centerLat, data.centerLng);
    const dimen: number = utils.kmToLat(data.dimen);
    const rects: Set<number> = grid.searchCollisionBoxes(center, dimen);
    const keywords: string = data.keywords;
    const limit: number = Math.pow(4, EarthQuadGrid.MAX_LEVELS - grid.getCollisionLevel(dimen));
    const halfDimen: number = dimen / 2;
    const topLeft: LatLng = new LatLng(center.latitude + halfDimen, center.longitude - halfDimen);
    const bottomRight: LatLng = new LatLng(center.latitude - halfDimen, center.longitude + halfDimen);
    const promises: Promise<FirebaseFirestore.QuerySnapshot>[] = [];
    let keywordArray: string[] = null;
    //Query firestore
    for(const rect of rects) {
        const limitRect: number = rect + limit;
        let colRef: FirebaseFirestore.Query = admin.firestore().collection('store')
                        .select('title', 'address', 'imageURL', 'rating', 'geo', 'keywords')
                        .where('gridNumber', '>=', rect)
                        .where('gridNumber', '<', limitRect);
        if(type !== undefined && type >= 0) {
            colRef = colRef.where('type', '==', type);
        }
        if(keywords !== undefined && keywords !== '') {
            keywordArray = keywords.split(/\s+/);
            colRef = colRef.where('keywords', 
            'array-contains', keywordArray[keywordArray.length - 1]);
            keywordArray.pop();
        }
        promises.push(colRef.get());
    }
    //proccess result
    return Promise.all(promises).then(snapshots => {
        const stores: Object[] = [];
        const distance2s: number[] = [];
        snapshots.forEach(snapshot => {
            snapshot.forEach(store => {
                const storeData: FirebaseFirestore.DocumentData = store.data();
                const location: LatLng = new LatLng(storeData.geo.latitude, 
                    storeData.geo.longitude);
                if(utils.isObjectInRect(topLeft, bottomRight, location) && 
                    utils.containsKeywords(keywordArray, storeData.keywords)) {
                        const newdata: Object = {
                            id : store.id,
                            title : storeData.title,
                            address : storeData.address,
                            imageURL : storeData.imageURL,
                            rating : storeData.rating,
                            geo : storeData.geo
                        }
                    distance2s.push(utils.distance2(location, center));
                    stores.push(newdata);
                    utils.sortAsc(distance2s, stores);
                }
            });
        });
        return stores.slice(from, from + C.NUM_NEARBY_STORES_BY_KEYWORDS);
    }).catch(err => {
        console.log(err);
        return [];
    });
});

/** input:
 * data.storeType
 * data.keywords
 * data.lastId
 */ 
export const storesByKeywords = functions.https.onCall(async (data, context)=> {
    if(data.keywords === undefined || data.keywords === '') {
        return [];
    } 
    const keywordArray: string[] = data.keywords.split(/\s+/);
    const type: number = data.storeType;
    let lastId: string = data.lastId; 
    const idField: FirebaseFirestore.FieldPath = admin.firestore.FieldPath.documentId();
    const stores: Object[] = [];
    let docCount: number = 0;
    let originQuery: FirebaseFirestore.Query = admin.firestore().collection('store')
                        .select('title', 'address', 'imageURL', 'rating', 'keywords');
    if( lastId === undefined || lastId === '') {
        lastId = '\0';
    }
    if(type !== undefined && type >= 0) {
        originQuery = originQuery.where('type', '==', type);
    }
    originQuery = originQuery.where('keywords', 'array-contains', keywordArray[keywordArray.length - 1]);
    keywordArray.pop();
    try {
        do {
            const promise = await originQuery.orderBy(idField).startAfter(lastId)
                            .limit(C.NUM_STORES_PER_QUERY).get();
            const docs: FirebaseFirestore.QueryDocumentSnapshot[] = promise.docs;
            docCount = docs.length;
            for(const doc of docs) {
                const storeData: FirebaseFirestore.DocumentData = doc.data();
                if(utils.containsKeywords(keywordArray, storeData.keywords)) {
                    const newdata: Object = {
                        id : doc.id,
                        title : storeData.title,
                        address : storeData.address,
                        imageURL : storeData.imageURL,
                        rating : storeData.rating
                    }
                    stores.push(newdata);
                    if(stores.length === C.NUM_STORES_BY_KEYWORDS) {
                        return stores;
                    }
                }
            }
            if(docCount > 0) {
                lastId = docs[docCount - 1].id;
            }
        } while(docCount > 0);
        return stores;
    } catch(err) {
        console.log(err);
        return [];
    }
});

/** input:
 * data.storeId : store id
 * data.lastId
 */
export const productsOfStore = functions.https.onCall(async (data, context) => {
    if(data.storeId === undefined || data.storeId === '') {
        return [];
    }
    const storeId: string = data.storeId;
    const idField: FirebaseFirestore.FieldPath = admin.firestore.FieldPath.documentId();
    let lastId: string = data.lastId;
    let docCount: number = 0;
    const products: Object[] = [];
    if(lastId === undefined || lastId === '') {
        lastId = '\0';
    }
    try {
        do {
            const promise = await admin.firestore().collection('product')
                                .select('title', 'imageURL', 'cost')
                                .where('storeId', '==', storeId).orderBy(idField)
                                .startAfter(lastId).limit(C.NUM_PRODUCTS_PER_QUERY)
                                .get();
            const docs: FirebaseFirestore.DocumentSnapshot[] = promise.docs;
            docCount = docs.length;
            for(const doc of docs) {
                const productData = doc.data();
                productData.id = doc.id;
                products.push(productData);
                if(products.length === C.NUM_PRODUCTS_OF_STORE) {
                    return products;
                }
            }
            if(docCount > 0) {
                lastId = docs[docCount - 1].id;
            }
        } while(docCount > 0);
        return products;
    } catch(err) {
        console.log(err);
        return [];
    }
});


/** input:
 * data.storeId: store id
 */
export const storeById = functions.https.onCall((data, context) => {
    if(data.storeId === undefined || data.storeId === '') {
        return null;
    }
    return admin.firestore().doc('store/' + data.storeId).get()
    .then(snapshot => {
        const storeData: FirebaseFirestore.DocumentData = snapshot.data();
        storeData.id = snapshot.id;
        return storeData;
    })
    .catch(err => {
        console.log(err);
        return null
    });
});

/** input:
 * data.productId : product id
 */
export const productById = functions.https.onCall((data, context) => {
    if(data.productId === undefined || data.productId === '') {
        return null;
    }
    return admin.firestore().doc('product/' + data.productId).get()
    .then(snapshot => {
        const productData: FirebaseFirestore.DocumentData = snapshot.data();
        productData.id = snapshot.id;
        return productData;
    })
    .catch(err => {
        console.log(err);
        return null;
    });
});

/**
 * input:
 * data.productType
 * data.keywords
 * data.lastId 
 */ 
export const productsByKeywords = functions.https.onCall(async (data, context) => {
    if(data.keywords === undefined || data.keywords === '') {
        return [];
    } 
    const keywordArray: string[] = data.keywords.split(/\s+/);
    const type: number = data.productType;
    let lastId: string = data.lastId; 
    const idField: FirebaseFirestore.FieldPath = admin.firestore.FieldPath.documentId();
    const products: Object[] = [];
    let docCount: number = 0;
    let originQuery: FirebaseFirestore.Query = admin.firestore().collection('product')
                        .select('title', 'address', 'imageURL', 'keywords', 'cost', 'geo');
    if(lastId === undefined || lastId === '') {
        lastId = '\0';
    }
    if(type !== undefined && type >= 0) {
        originQuery = originQuery.where('type', '==', type);
    }
    originQuery = originQuery.where('keywords', 'array-contains', keywordArray[keywordArray.length - 1]);
    keywordArray.pop();
    try {
        do {
            const promise = await originQuery.orderBy(idField).startAfter(lastId)
                            .limit(C.NUM_PRODUCTS_PER_QUERY).get();
            const docs: FirebaseFirestore.DocumentSnapshot[] = promise.docs;
            docCount = docs.length;
            for(const doc of docs) {
                const productData: FirebaseFirestore.DocumentData = doc.data();
                if(utils.containsKeywords(keywordArray, productData.keywords)) {
                    const newdata: Object = {
                        id : doc.id,
                        title : productData.title,
                        address : productData.address,
                        imageURL : productData.imageURL,
                        cost : productData.cost,
                        geo : productData.geo
                    }
                    products.push(newdata);
                    if(products.length === C.NUM_PRODUCTS_BY_KEYWORDS) {
                        return products;
                    }
                }
            }
            if(docCount > 0) {
                lastId = docs[docCount - 1].id;
            }
        } while(docCount > 0);
        return products;
    } catch(err) {
        console.log(err);
        return [];
    }
});

export const nearbyProducts = functions.https.onCall((data, context) => {
    const grid: EarthQuadGrid = EarthQuadGrid.instance;
    const from: number = data.from;
    const type: number = data.productType;
    const center: LatLng = new LatLng(data.centerLat, data.centerLng);
    const dimen: number = utils.kmToLat(C.NEARBY_DIMEN);
    const rects: Set<number> = grid.searchCollisionBoxes(center, dimen);
    const limit: number = Math.pow(4, EarthQuadGrid.MAX_LEVELS - grid.getCollisionLevel(dimen));
    const halfDimen: number = dimen / 2;
    const topLeft: LatLng = new LatLng(center.latitude + halfDimen, center.longitude - halfDimen);
    const bottomRight: LatLng = new LatLng(center.latitude - halfDimen, center.longitude + halfDimen);
    const promises: Promise<FirebaseFirestore.QuerySnapshot>[] = [];
    for(const rect of rects) {
        const limitRect: number = rect + limit;
        let colRef: FirebaseFirestore.Query = admin.firestore().collection('product')
                        .select('title', 'address', 'imageURL', 'storeId', 'geo', 'cost')
                        .where('gridNumber', '>=', rect)
                        .where('gridNumber', '<', limitRect);
        if(type !== undefined && type >= 0) {
            colRef = colRef.where('type', '==', type);
        }
        promises.push(colRef.get());
    }
    return Promise.all(promises).then(snapshots => {
        const products: Object[] = [];
        snapshots.forEach(snapshot => {
            snapshot.forEach(product => {
                const productData: FirebaseFirestore.DocumentData = product.data();
                const location: LatLng = new LatLng(productData.geo.latitude, 
                    productData.geo.longitude);
                if(utils.isObjectInRect(topLeft, bottomRight, location)) {
                    const newdata: Object = {
                        id : product.id,
                        title : productData.title,
                        storeId : productData.storeId,
                        address : productData.address,
                        imageURL : productData.imageURL,
                        geo : productData.geo,
                        cost : productData.cost
                    }
                    products.push(newdata);
                }
            });
        });
        return products.slice(from, from + C.NUM_NEARBY_PRODUCTS);
    }).catch(err => {
        console.log(err);
        return [];
    });
});



import 'package:cloud_firestore/cloud_firestore.dart';

import 'phone.dart';

class PhoneRepository {
  PhoneRepository({FirebaseFirestore? firestore})
      : _collection =
            (firestore ?? FirebaseFirestore.instance).collection('phones');

  final CollectionReference<Map<String, dynamic>> _collection;

  Stream<List<Phone>> observePhones() {
    return _collection
        .orderBy('createdAt', descending: true)
        .snapshots()
        .map((snapshot) {
      return snapshot.docs
          .map((doc) => Phone.fromMap(doc.id, doc.data()))
          .toList();
    });
  }

  Future<void> savePhone(Phone phone) {
    return _collection.doc(phone.id).set(phone.toMap());
  }

  Future<void> deletePhone(String id) {
    return _collection.doc(id).delete();
  }
}

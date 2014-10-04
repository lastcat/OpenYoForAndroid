OpenYoForAndroid
================

OepnYoのandroidクライアントです。

#概要
OpenYo[https://github.com/nna774/OpenYo]のAndroid用クライアント（saample）です。中のOpenYoクラスについてはライブラリ化したいと考思っているので、自作クライアントを作られる場合はそちらをご利用ください。
#ビルド方法
submoduleでvolleyを使用をしているので、'git clone'して、modules/volley以下で'git submodule init'して'git submodule update'して下さい。

#アプリの使い方
最初に、まずエンドポイントを登録します。EndPointURLにエンドポイントのURLを入力してSave EndPointボタンをタッチしてください。

次に指定したエンドポイントのOpenYoに対してSign upします。new user nameにユーザー名を、passwordにパスワードを入力してSign Upボタンをタッチしてください。
そのアカウントを永続使用したい場合はSave Accountボタンをタッチしてください。内部DBにアカウントが保存されます。
Yoを送りたい場合はto user nameに送先のuser名を指定してsend Yoボタンをタッチしてしてください。sendAllは今までYoを送ったことのある人すべてにYoを送ります。
CountTotalFriendsとListFriendsは今のところresponseを返してるだけです……

バグ、ご要望がありましたらissueまで。（たくさんあると思います）


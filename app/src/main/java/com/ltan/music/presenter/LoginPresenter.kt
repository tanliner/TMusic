package com.ltan.music.presenter

import com.ltan.music.UserApi
import com.ltan.music.business.api.*
import com.ltan.music.common.MusicLog
import com.ltan.music.contract.LoginContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber

/**
 * TMusic.com.ltan.music.presenter
 *
 * @ClassName: LoginPresenter
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
class LoginPresenter : RxPresenter<LoginContract.View>(), LoginContract.Presenter {
    override fun login(name: String, pass: String) {
        ApiProxy.instance.getApi(UserApi::class.java)
            .login(name, pass)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .subscribe(object : NormalSubscriber<Any>(), Subscriber<Any> {
                override fun onNext(t: Any) {
                    MusicLog.d("LoginPresenter", "onNext   $t")
                }

                override fun onError(errorCode: Int, errorMsg: String) {
                    MusicLog.d("LoginPresenter", "onError error code $errorCode")
                }

            })
        val a =
            "{\"popAdjust\":false,\"data\":[{\"name\":\"可惜没如果\",\"id\":29814898,\"position\":7,\"alias\":[\"If Only…\"],\"status\":0,\"fee\":8,\"copyrightId\":7002,\"disc\":\"1\",\"no\":7,\"artists\":" +
                    "[{\"name\":\"林俊杰\",\"id\":3684,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":" +
                    "\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0}],\"album\":{\"name\":\"新地球\",\"id\":3056951,\"type\":" +
                    "\"专辑\",\"size\":13,\"picId\":3238061746556733,\"blurPicUrl\":\"http://p2.music.126.net/X0EDfXzxMQJiQ-71JFGdZw==/3238061746556733.jpg\",\"companyId\":0,\"pic\":3238061746556733,\"picUrl\":" +
                    "\"http://p2.music.126.net/X0EDfXzxMQJiQ-71JFGdZw==/3238061746556733.jpg\",\"publishTime\":1419609600007,\"description\":\"\",\"tags\":\"\",\"company\":\"华纳唱片\",\"briefDesc\":\"\",\"artist\":" +
                    "{\"name\":\"\",\"id\":0,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":" +
                    "\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0},\"songs\":[],\"alias\":[],\"status\":3,\"copyrightId\":" +
                    "7002,\"commentThreadId\":\"R_AL_3_3056951\",\"artists\":[{\"name\":\"林俊杰\",\"id\":3684,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":" +
                    "\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":" +
                    "\"\",\"musicSize\":0}],\"subType\":\"录音室版\",\"transName\":null},\"starred\":false,\"popularity\":100.0,\"score\":100,\"starredNum\":0,\"duration\":298276,\"playedNum\":0,\"dayPlays\":0," +
                    "\"hearTime\":0,\"ringtone\":null,\"crbt\":null,\"audition\":null,\"copyFrom\":\"\",\"commentThreadId\":\"R_SO_4_29814898\",\"rtUrl\":null,\"ftype\":0,\"rtUrls\":[],\"copyright\":1,\"transName\":" +
                    "null,\"sign\":null,\"hMusic\":{\"name\":null,\"id\":1186556209,\"size\":11933822,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":320000,\"playTime\":298276,\"volumeDelta\":-37800.0},\"mMusic\":" +
                    "{\"name\":null,\"id\":1186556210,\"size\":7160310,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":192000,\"playTime\":298276,\"volumeDelta\":-35300.0},\"lMusic\":{\"name\":null,\"id\":1186556211," +
                    "\"size\":4773554,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":128000,\"playTime\":298276,\"volumeDelta\":-33500.0},\"bMusic\":{\"name\":null,\"id\":1186556211,\"size\":4773554,\"extension\":" +
                    "\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":128000,\"playTime\":298276,\"volumeDelta\":-33500.0},\"rtype\":0,\"rurl\":null,\"mvid\":375130,\"mp3Url\":null,\"alg\":\"default\"},{\"name\":\"アンビリーバーズ\"," +
                    "\"id\":35270825,\"position\":1,\"alias\":[],\"status\":0,\"fee\":16,\"copyrightId\":7003,\"disc\":\"1\",\"no\":1,\"artists\":[{\"name\":\"米津玄師\",\"id\":159300,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":" +
                    "\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0}]," +
                    "\"album\":{\"name\":\"Bremen\",\"id\":3308845,\"type\":\"专辑\",\"size\":14,\"picId\":7986852466891427,\"blurPicUrl\":\"http://p2.music.126.net/jnSQrChlJLCgu77hCe7RBQ==/7986852466891427.jpg\",\"companyId\":0,\"pic\":" +
                    "7986852466891427,\"picUrl\":\"http://p2.music.126.net/jnSQrChlJLCgu77hCe7RBQ==/7986852466891427.jpg\",\"publishTime\":1444147200007,\"description\":\"\",\"tags\":\"\",\"company\":\"环球唱片\",\"briefDesc\":\"\",\"artist\":" +
                    "{\"name\":\"\",\"id\":0,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":" +
                    "\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0},\"songs\":[],\"alias\":[],\"status\":-1,\"copyrightId\":7003,\"commentThreadId\":" +
                    "\"R_AL_3_3308845\",\"artists\":[{\"name\":\"米津玄師\",\"id\":159300,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":" +
                    "\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0}],\"subType\":\"录音室版\",\"transName\":null},\"starred\":false,\"popularity\":" +
                    "65.0,\"score\":65,\"starredNum\":0,\"duration\":286537,\"playedNum\":0,\"dayPlays\":0,\"hearTime\":0,\"ringtone\":null,\"crbt\":null,\"audition\":null,\"copyFrom\":\"\",\"commentThreadId\":\"R_SO_4_35270825\",\"rtUrl\":" +
                    "null,\"ftype\":0,\"rtUrls\":[],\"copyright\":0,\"transName\":null,\"sign\":null,\"hMusic\":{\"name\":null,\"id\":106923088,\"size\":11463618,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":320000,\"playTime\":286537," +
                    "\"volumeDelta\":-3.69},\"mMusic\":{\"name\":null,\"id\":106923089,\"size\":5731831,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":160000,\"playTime\":286537,\"volumeDelta\":-3.27},\"lMusic\":{\"name\":null,\"id\":" +
                    "106923090,\"size\":3439116,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":96000,\"playTime\":286537,\"volumeDelta\":-3.32},\"bMusic\":{\"name\":null,\"id\":106923090,\"size\":3439116,\"extension\":\"mp3\",\"sr\":" +
                    "44100,\"dfsId\":0,\"bitrate\":96000,\"playTime\":286537,\"volumeDelta\":-3.32},\"rtype\":0,\"rurl\":null,\"mvid\":0,\"mp3Url\":null,\"alg\":\"default\"},{\"name\":\"Down\",\"id\":405226741,\"position\":1,\"alias\":[],\"status\":" +
                    "0,\"fee\":8,\"copyrightId\":7003,\"disc\":\"1\",\"no\":1,\"artists\":[{\"name\":\"Marian Hill\",\"id\":893433,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\"," +
                    "\"img1v1Url\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0}],\"album\":{\"name\":\"Down\",\"id\":34504423,\"type\":\"EP/Single\",\"size\":" +
                    "1,\"picId\":17720828905014989,\"blurPicUrl\":\"http://p2.music.126.net/cRBwduKPIRgRnpQVYHhwdQ==/17720828905014989.jpg\",\"companyId\":0,\"pic\":17720828905014989,\"picUrl\":" +
                    "\"http://p2.music.126.net/cRBwduKPIRgRnpQVYHhwdQ==/17720828905014989.jpg\",\"publishTime\":1457020800007,\"description\":\"\",\"tags\":\"\",\"company\":\"环球唱片\",\"briefDesc\":\"\",\"artist\":" +
                    "{\"name\":\"\",\"id\":0,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"img1v1Url\":" +
                    "\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0},\"songs\":[],\"alias\":[],\"status\":3,\"copyrightId\":" +
                    "7003,\"commentThreadId\":\"R_AL_3_34504423\",\"artists\":[{\"name\":\"Marian Hill\",\"id\":893433,\"picId\":0,\"img1v1Id\":0,\"briefDesc\":\"\",\"picUrl\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\"," +
                    "\"img1v1Url\":\"http://p2.music.126.net/6y-UleORITEDbvrOLV0Q8A==/5639395138885805.jpg\",\"albumSize\":0,\"alias\":[],\"trans\":\"\",\"musicSize\":0}],\"subType\":\"录音室版\",\"transName\":null,\"picId_str\":" +
                    "\"17720828905014989\"},\"starred\":false,\"popularity\":100.0,\"score\":100,\"starredNum\":0,\"duration\":197851,\"playedNum\":0,\"dayPlays\":0,\"hearTime\":0,\"ringtone\":null,\"crbt\":null,\"audition\":null," +
                    "\"copyFrom\":\"\",\"commentThreadId\":\"R_SO_4_405226741\",\"rtUrl\":null,\"ftype\":0,\"rtUrls\":[],\"copyright\":1,\"transName\":null,\"sign\":null,\"hMusic\":{\"name\":null,\"id\":1194739917,\"size\":7916190," +
                    "\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":320000,\"playTime\":197851,\"volumeDelta\":-2.65076E-4},\"mMusic\":{\"name\":null,\"id\":1194739918,\"size\":3958117,\"extension\":\"mp3\",\"sr\":44100," +
                    "\"dfsId\":0,\"bitrate\":160000,\"playTime\":197851,\"volumeDelta\":-2.65076E-4},\"lMusic\":{\"name\":null,\"id\":1194739919,\"size\":2374888,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":96000,\"playTime\":" +
                    "197851,\"volumeDelta\":-2.65076E-4},\"bMusic\":{\"name\":null,\"id\":1194739919,\"size\":2374888,\"extension\":\"mp3\",\"sr\":44100,\"dfsId\":0,\"bitrate\":96000,\"playTime\":197851,\"volumeDelta\":-2.65076E-4}," +
                    "\"rtype\":0,\"rurl\":null,\"mvid\":5463034,\"mp3Url\":null,\"alg\":\"default\"}],\"code\":200}"
    }

    override fun query() {
        ApiProxy.instance.getApi(UserApi::class.java)
            .query()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .safeSubscribe(object : NormalSubscriber<String>() {
                override fun onNext(t: String) {
                    MusicLog.d("LoginPresenter", "query onNext any\n$t ")
                }

                override fun onComplete() {
                    MusicLog.d("LoginPresenter", "query onComplete")
                }
            })
    }

    override fun test() {
        ApiProxy.instance.getApi(UserApi::class.java)
            .test()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .safeSubscribe(object : NormalSubscriber<Module<List<Root>>>() {
                override fun onNext(t: Module<List<Root>>) {
                    MusicLog.d("LoginPresenter", "test onNext any $t")
                }

                override fun onComplete() {
                    MusicLog.d("LoginPresenter", "test onComplete")
                }
            })
    }

}
package com.ltan.music.business.api

/**
 * TMusic.com.ltan.music.business.api
 *
 * @ClassName: Album
 * @Description:
 * @Author: tanlin
 * @Date:   2019-05-12
 * @Version: 1.0
 */
class Album {
    private var name: String? = null

    private var id: Int = 0

    private var type: String? = null

    private var size: Int = 0

    private var picId: Long = 0L

    private var blurPicUrl: String? = null

    private var companyId: Int = 0

    private var pic: Long = 0

    private var picUrl: String? = null

    private var publishTime: Long = 0

    private var description: String? = null

    private var tags: String? = null

    private var company: String? = null

    private var briefDesc: String? = null

    private var artist: Artist? = null

    private var songs: List<Songs>? = null

    private var alias: List<String>? = null

    private var status: Int = 0

    private var copyrightId: Int = 0

    private var commentThreadId: String? = null

    private var artists: List<Artists>? = null

    private var subType: String? = null

    private var transName: String? = null

    fun setName(name: String) {
        this.name = name
    }

    fun getName(): String? {
        return this.name
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getId(): Int {
        return this.id
    }

    fun setType(type: String) {
        this.type = type
    }

    fun getType(): String? {
        return this.type
    }

    fun setSize(size: Int) {
        this.size = size
    }

    fun getSize(): Int {
        return this.size
    }

    fun setPicId(picId: Long) {
        this.picId = picId
    }

    fun getPicId(): Long {
        return this.picId
    }

    fun setBlurPicUrl(blurPicUrl: String) {
        this.blurPicUrl = blurPicUrl
    }

    fun getBlurPicUrl(): String? {
        return this.blurPicUrl
    }

    fun setCompanyId(companyId: Int) {
        this.companyId = companyId
    }

    fun getCompanyId(): Int {
        return this.companyId
    }

    fun setPic(pic: Long) {
        this.pic = pic
    }

    fun getPic(): Long {
        return this.pic
    }

    fun setPicUrl(picUrl: String) {
        this.picUrl = picUrl
    }

    fun getPicUrl(): String? {
        return this.picUrl
    }

    fun setPublishTime(publishTime: Long) {
        this.publishTime = publishTime
    }

    fun getPublishTime(): Long {
        return this.publishTime
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun getDescription(): String? {
        return this.description
    }

    fun setTags(tags: String) {
        this.tags = tags
    }

    fun getTags(): String? {
        return this.tags
    }

    fun setCompany(company: String) {
        this.company = company
    }

    fun getCompany(): String? {
        return this.company
    }

    fun setBriefDesc(briefDesc: String) {
        this.briefDesc = briefDesc
    }

    fun getBriefDesc(): String? {
        return this.briefDesc
    }

    fun setArtist(artist: Artist) {
        this.artist = artist
    }

    fun getArtist(): Artist? {
        return this.artist
    }

    fun setSongs(songs: List<Songs>) {
        this.songs = songs
    }

    fun getSongs(): List<Songs>? {
        return this.songs
    }

    fun setAlias(alias: List<String>) {
        this.alias = alias
    }

    fun getAlias(): List<String>? {
        return this.alias
    }

    fun setStatus(status: Int) {
        this.status = status
    }

    fun getStatus(): Int {
        return this.status
    }

    fun setCopyrightId(copyrightId: Int) {
        this.copyrightId = copyrightId
    }

    fun getCopyrightId(): Int {
        return this.copyrightId
    }

    fun setCommentThreadId(commentThreadId: String) {
        this.commentThreadId = commentThreadId
    }

    fun getCommentThreadId(): String? {
        return this.commentThreadId
    }

    fun setArtists(artists: List<Artists>) {
        this.artists = artists
    }

    fun getArtists(): List<Artists>? {
        return this.artists
    }

    fun setSubType(subType: String) {
        this.subType = subType
    }

    fun getSubType(): String? {
        return this.subType
    }

    fun setTransName(transName: String) {
        this.transName = transName
    }

    fun getTransName(): String? {
        return this.transName
    }
}
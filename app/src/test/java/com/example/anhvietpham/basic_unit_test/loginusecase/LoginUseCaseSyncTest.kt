package com.example.anhvietpham.basic_unit_test.loginusecase

import com.example.anhvietpham.basic_unit_test.loginusecase.authtoken.AuthTokenCache
import com.example.anhvietpham.basic_unit_test.loginusecase.eventbus.EventBusPoster
import com.example.anhvietpham.basic_unit_test.loginusecase.networking.LoginHttpEndpointSync
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
const val USER_NAME = "username"
const val AUTH_TOKEN = "authToken"
const val PASSWORD = "password"
class LoginUseCaseSyncTest{
    private lateinit var SUT : LoginUseCaseSync
    private lateinit var loginHttpEndpointSyncTd: LoginHttpEndpointSyncTd
    private lateinit var authTokenCacheTd: AuthTokenCacheTd
    private lateinit var eventBusPosterTd: EnventBusPosterTd

    @Before
    fun setUp() {
        loginHttpEndpointSyncTd = LoginHttpEndpointSyncTd()
        authTokenCacheTd = AuthTokenCacheTd()
        eventBusPosterTd = EnventBusPosterTd()
        SUT = LoginUseCaseSync(
            loginHttpEndpointSyncTd,
            authTokenCacheTd,
            eventBusPosterTd
        )
    }

    // Username and password passed to the endpoint
    @Test
    fun loginSync_success_usernameAndPasswordPassedToEndpoint() {
        SUT.loginSync(userName = USER_NAME, password = PASSWORD)
        assertThat(loginHttpEndpointSyncTd.mUserName, `is`(USER_NAME))
        assertThat(loginHttpEndpointSyncTd.mPassword, `is`(PASSWORD))
    }

    // If login succeeds - user's autho token must be cached.
    @Test
    fun loginSync_success_authTokenCached() {
        SUT.loginSync(userName = USER_NAME, password = PASSWORD)
        assertThat(authTokenCacheTd.mAuthToken, `is`(AUTH_TOKEN))
    }

    // If login fails - auth token is not change.
    @Test
    fun loginSync_generalError_authTokenNotCached() {
        loginHttpEndpointSyncTd.isGeneralError = true
        SUT.loginSync(USER_NAME, PASSWORD)
        assertThat(authTokenCacheTd.mAuthToken, `is`(""))
    }

    @Test
    fun loginSync_authError_authTokenNotCached() {
        loginHttpEndpointSyncTd.isAuthError = true
        SUT.loginSync(USER_NAME, PASSWORD)
        assertThat(authTokenCacheTd.mAuthToken, `is`(""))
    }

    @Test
    fun loginSync_serverError_authTokenNotCached() {
        loginHttpEndpointSyncTd.isServerError = true
        SUT.loginSync(USER_NAME, PASSWORD)
        assertThat(authTokenCacheTd.mAuthToken, `is`(""))
    }
    // If login succeeds -  login event posted to event bus.
    // If login fail - no login event posted.
    // If login succeeds - success returned
    // If fails -  fail returned
    // network - network error returned

    private class LoginHttpEndpointSyncTd : LoginHttpEndpointSync {
        var mUserName: String? = null
        var mPassword: String? = null
        var isGeneralError: Boolean = false
        var isAuthError: Boolean = false
        var isServerError: Boolean = false

        override fun loginSync(usename: String, password: String): LoginHttpEndpointSync.EndpointResult {
            mUserName = usename
            mPassword = password
            return when {
                isGeneralError -> LoginHttpEndpointSync.EndpointResult(
                    mStatus = LoginHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
                    mAuthToken = ""
                )
                isAuthError -> LoginHttpEndpointSync.EndpointResult(
                    mStatus = LoginHttpEndpointSync.EndpointResultStatus.AUTH_ERROR,
                    mAuthToken = ""
                )
                isServerError -> LoginHttpEndpointSync.EndpointResult(
                    mStatus = LoginHttpEndpointSync.EndpointResultStatus.SERVER_ERROR,
                    mAuthToken = ""
                )
                else -> LoginHttpEndpointSync.EndpointResult(
                    mStatus = LoginHttpEndpointSync.EndpointResultStatus.SUCCESS,
                    mAuthToken = AUTH_TOKEN
                )
            }
        }
    }

    private class AuthTokenCacheTd : AuthTokenCache {
        var mAuthToken: String = ""
        override fun cacheAuthToken(authtoken: String) {
            mAuthToken = authtoken
        }
        override fun getAuthToken(): String {
            return mAuthToken!!
        }
    }

    private class EnventBusPosterTd: EventBusPoster{
        override fun postEvent(event: Object) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}
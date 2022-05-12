import React, {useContext, useState} from 'react'
import {useHistory} from 'react-router-dom'
import { TokenContext } from '../../contexts'
import {post} from "../../services/api"
import {UserAndPass} from "./UserAndPass"

export const Login = () => {
  let history = useHistory()

  let [user, setUser] = useState("")
  let [pass, setPass] = useState("")
  let [error, seterror] = useState()
  let [code, setcode] = useState()

  let [, setToken] = useContext(TokenContext)

  const loginUser = () => {
    if (user && pass) {
      post("admin/login", {
        name: user,
        password: pass
      }).then((t) => {
          console.log(t)
          if(t.token === "step-2") {
            setcode(true)
          } else {
            setToken(t.token)
            history.push('/dash')
          }
        }
      ).catch((e) => seterror(e))
    }
  }

  return <div className="pt-4">

    <UserAndPass setUser={setUser} setPass={setPass}/>
    {error && <p className="text-red-400 pb-2">Whoops, is your user and pass correct?</p> }
    {code && <p className="text-green-400 pb-2">Please check your inbox and enter the verification code into the password box!</p> }
    <button
      className="bg-blue-500 text-white active:bg-pink-600 font-bold uppercase text-sm px-6 py-3 rounded shadow hover:shadow-lg outline-none focus:outline-none mr-1 mb-1 ease-linear transition-all duration-150"
      type="button"
      onClick={loginUser}
    >
      Login
    </button>
  </div>
}
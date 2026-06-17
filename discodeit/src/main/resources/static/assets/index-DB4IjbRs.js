var Vg=Object.defineProperty;var Wg=(n,i,s)=>i in n?Vg(n,i,{enumerable:!0,configurable:!0,writable:!0,value:s}):n[i]=s;var kf=(n,i,s)=>Wg(n,typeof i!="symbol"?i+"":i,s);(function(){const i=document.createElement("link").relList;if(i&&i.supports&&i.supports("modulepreload"))return;for(const c of document.querySelectorAll('link[rel="modulepreload"]'))l(c);new MutationObserver(c=>{for(const d of c)if(d.type==="childList")for(const f of d.addedNodes)f.tagName==="LINK"&&f.rel==="modulepreload"&&l(f)}).observe(document,{childList:!0,subtree:!0});function s(c){const d={};return c.integrity&&(d.integrity=c.integrity),c.referrerPolicy&&(d.referrerPolicy=c.referrerPolicy),c.crossOrigin==="use-credentials"?d.credentials="include":c.crossOrigin==="anonymous"?d.credentials="omit":d.credentials="same-origin",d}function l(c){if(c.ep)return;c.ep=!0;const d=s(c);fetch(c.href,d)}})();function mu(n){return n&&n.__esModule&&Object.prototype.hasOwnProperty.call(n,"default")?n.default:n}var Ma={exports:{}},Co={},_a={exports:{}},ke={};/**
 * @license React
 * react.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */var Cf;function qg(){if(Cf)return ke;Cf=1;var n=Symbol.for("react.element"),i=Symbol.for("react.portal"),s=Symbol.for("react.fragment"),l=Symbol.for("react.strict_mode"),c=Symbol.for("react.profiler"),d=Symbol.for("react.provider"),f=Symbol.for("react.context"),m=Symbol.for("react.forward_ref"),x=Symbol.for("react.suspense"),y=Symbol.for("react.memo"),S=Symbol.for("react.lazy"),j=Symbol.iterator;function $(w){return w===null||typeof w!="object"?null:(w=j&&w[j]||w["@@iterator"],typeof w=="function"?w:null)}var I={isMounted:function(){return!1},enqueueForceUpdate:function(){},enqueueReplaceState:function(){},enqueueSetState:function(){}},k=Object.assign,A={};function _(w,L,ie){this.props=w,this.context=L,this.refs=A,this.updater=ie||I}_.prototype.isReactComponent={},_.prototype.setState=function(w,L){if(typeof w!="object"&&typeof w!="function"&&w!=null)throw Error("setState(...): takes an object of state variables to update or a function which returns an object of state variables.");this.updater.enqueueSetState(this,w,L,"setState")},_.prototype.forceUpdate=function(w){this.updater.enqueueForceUpdate(this,w,"forceUpdate")};function J(){}J.prototype=_.prototype;function G(w,L,ie){this.props=w,this.context=L,this.refs=A,this.updater=ie||I}var H=G.prototype=new J;H.constructor=G,k(H,_.prototype),H.isPureReactComponent=!0;var X=Array.isArray,D=Object.prototype.hasOwnProperty,N={current:null},Q={key:!0,ref:!0,__self:!0,__source:!0};function le(w,L,ie){var ae,de={},he=null,we=null;if(L!=null)for(ae in L.ref!==void 0&&(we=L.ref),L.key!==void 0&&(he=""+L.key),L)D.call(L,ae)&&!Q.hasOwnProperty(ae)&&(de[ae]=L[ae]);var ye=arguments.length-2;if(ye===1)de.children=ie;else if(1<ye){for(var xe=Array(ye),Ee=0;Ee<ye;Ee++)xe[Ee]=arguments[Ee+2];de.children=xe}if(w&&w.defaultProps)for(ae in ye=w.defaultProps,ye)de[ae]===void 0&&(de[ae]=ye[ae]);return{$$typeof:n,type:w,key:he,ref:we,props:de,_owner:N.current}}function Se(w,L){return{$$typeof:n,type:w.type,key:L,ref:w.ref,props:w.props,_owner:w._owner}}function ge(w){return typeof w=="object"&&w!==null&&w.$$typeof===n}function pe(w){var L={"=":"=0",":":"=2"};return"$"+w.replace(/[=:]/g,function(ie){return L[ie]})}var Be=/\/+/g;function Fe(w,L){return typeof w=="object"&&w!==null&&w.key!=null?pe(""+w.key):L.toString(36)}function V(w,L,ie,ae,de){var he=typeof w;(he==="undefined"||he==="boolean")&&(w=null);var we=!1;if(w===null)we=!0;else switch(he){case"string":case"number":we=!0;break;case"object":switch(w.$$typeof){case n:case i:we=!0}}if(we)return we=w,de=de(we),w=ae===""?"."+Fe(we,0):ae,X(de)?(ie="",w!=null&&(ie=w.replace(Be,"$&/")+"/"),V(de,L,ie,"",function(Ee){return Ee})):de!=null&&(ge(de)&&(de=Se(de,ie+(!de.key||we&&we.key===de.key?"":(""+de.key).replace(Be,"$&/")+"/")+w)),L.push(de)),1;if(we=0,ae=ae===""?".":ae+":",X(w))for(var ye=0;ye<w.length;ye++){he=w[ye];var xe=ae+Fe(he,ye);we+=V(he,L,ie,xe,de)}else if(xe=$(w),typeof xe=="function")for(w=xe.call(w),ye=0;!(he=w.next()).done;)he=he.value,xe=ae+Fe(he,ye++),we+=V(he,L,ie,xe,de);else if(he==="object")throw L=String(w),Error("Objects are not valid as a React child (found: "+(L==="[object Object]"?"object with keys {"+Object.keys(w).join(", ")+"}":L)+"). If you meant to render a collection of children, use an array instead.");return we}function z(w,L,ie){if(w==null)return w;var ae=[],de=0;return V(w,ae,"","",function(he){return L.call(ie,he,de++)}),ae}function b(w){if(w._status===-1){var L=w._result;L=L(),L.then(function(ie){(w._status===0||w._status===-1)&&(w._status=1,w._result=ie)},function(ie){(w._status===0||w._status===-1)&&(w._status=2,w._result=ie)}),w._status===-1&&(w._status=0,w._result=L)}if(w._status===1)return w._result.default;throw w._result}var W={current:null},P={transition:null},F={ReactCurrentDispatcher:W,ReactCurrentBatchConfig:P,ReactCurrentOwner:N};function B(){throw Error("act(...) is not supported in production builds of React.")}return ke.Children={map:z,forEach:function(w,L,ie){z(w,function(){L.apply(this,arguments)},ie)},count:function(w){var L=0;return z(w,function(){L++}),L},toArray:function(w){return z(w,function(L){return L})||[]},only:function(w){if(!ge(w))throw Error("React.Children.only expected to receive a single React element child.");return w}},ke.Component=_,ke.Fragment=s,ke.Profiler=c,ke.PureComponent=G,ke.StrictMode=l,ke.Suspense=x,ke.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED=F,ke.act=B,ke.cloneElement=function(w,L,ie){if(w==null)throw Error("React.cloneElement(...): The argument must be a React element, but you passed "+w+".");var ae=k({},w.props),de=w.key,he=w.ref,we=w._owner;if(L!=null){if(L.ref!==void 0&&(he=L.ref,we=N.current),L.key!==void 0&&(de=""+L.key),w.type&&w.type.defaultProps)var ye=w.type.defaultProps;for(xe in L)D.call(L,xe)&&!Q.hasOwnProperty(xe)&&(ae[xe]=L[xe]===void 0&&ye!==void 0?ye[xe]:L[xe])}var xe=arguments.length-2;if(xe===1)ae.children=ie;else if(1<xe){ye=Array(xe);for(var Ee=0;Ee<xe;Ee++)ye[Ee]=arguments[Ee+2];ae.children=ye}return{$$typeof:n,type:w.type,key:de,ref:he,props:ae,_owner:we}},ke.createContext=function(w){return w={$$typeof:f,_currentValue:w,_currentValue2:w,_threadCount:0,Provider:null,Consumer:null,_defaultValue:null,_globalName:null},w.Provider={$$typeof:d,_context:w},w.Consumer=w},ke.createElement=le,ke.createFactory=function(w){var L=le.bind(null,w);return L.type=w,L},ke.createRef=function(){return{current:null}},ke.forwardRef=function(w){return{$$typeof:m,render:w}},ke.isValidElement=ge,ke.lazy=function(w){return{$$typeof:S,_payload:{_status:-1,_result:w},_init:b}},ke.memo=function(w,L){return{$$typeof:y,type:w,compare:L===void 0?null:L}},ke.startTransition=function(w){var L=P.transition;P.transition={};try{w()}finally{P.transition=L}},ke.unstable_act=B,ke.useCallback=function(w,L){return W.current.useCallback(w,L)},ke.useContext=function(w){return W.current.useContext(w)},ke.useDebugValue=function(){},ke.useDeferredValue=function(w){return W.current.useDeferredValue(w)},ke.useEffect=function(w,L){return W.current.useEffect(w,L)},ke.useId=function(){return W.current.useId()},ke.useImperativeHandle=function(w,L,ie){return W.current.useImperativeHandle(w,L,ie)},ke.useInsertionEffect=function(w,L){return W.current.useInsertionEffect(w,L)},ke.useLayoutEffect=function(w,L){return W.current.useLayoutEffect(w,L)},ke.useMemo=function(w,L){return W.current.useMemo(w,L)},ke.useReducer=function(w,L,ie){return W.current.useReducer(w,L,ie)},ke.useRef=function(w){return W.current.useRef(w)},ke.useState=function(w){return W.current.useState(w)},ke.useSyncExternalStore=function(w,L,ie){return W.current.useSyncExternalStore(w,L,ie)},ke.useTransition=function(){return W.current.useTransition()},ke.version="18.3.1",ke}var Ef;function gu(){return Ef||(Ef=1,_a.exports=qg()),_a.exports}/**
 * @license React
 * react-jsx-runtime.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */var jf;function Qg(){if(jf)return Co;jf=1;var n=gu(),i=Symbol.for("react.element"),s=Symbol.for("react.fragment"),l=Object.prototype.hasOwnProperty,c=n.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED.ReactCurrentOwner,d={key:!0,ref:!0,__self:!0,__source:!0};function f(m,x,y){var S,j={},$=null,I=null;y!==void 0&&($=""+y),x.key!==void 0&&($=""+x.key),x.ref!==void 0&&(I=x.ref);for(S in x)l.call(x,S)&&!d.hasOwnProperty(S)&&(j[S]=x[S]);if(m&&m.defaultProps)for(S in x=m.defaultProps,x)j[S]===void 0&&(j[S]=x[S]);return{$$typeof:i,type:m,key:$,ref:I,props:j,_owner:c.current}}return Co.Fragment=s,Co.jsx=f,Co.jsxs=f,Co}var Af;function Gg(){return Af||(Af=1,Ma.exports=Qg()),Ma.exports}var h=Gg(),Z=gu();const St=mu(Z);var Ki={},Ta={exports:{}},ht={},Na={exports:{}},$a={};/**
 * @license React
 * scheduler.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */var Rf;function Kg(){return Rf||(Rf=1,function(n){function i(P,F){var B=P.length;P.push(F);e:for(;0<B;){var w=B-1>>>1,L=P[w];if(0<c(L,F))P[w]=F,P[B]=L,B=w;else break e}}function s(P){return P.length===0?null:P[0]}function l(P){if(P.length===0)return null;var F=P[0],B=P.pop();if(B!==F){P[0]=B;e:for(var w=0,L=P.length,ie=L>>>1;w<ie;){var ae=2*(w+1)-1,de=P[ae],he=ae+1,we=P[he];if(0>c(de,B))he<L&&0>c(we,de)?(P[w]=we,P[he]=B,w=he):(P[w]=de,P[ae]=B,w=ae);else if(he<L&&0>c(we,B))P[w]=we,P[he]=B,w=he;else break e}}return F}function c(P,F){var B=P.sortIndex-F.sortIndex;return B!==0?B:P.id-F.id}if(typeof performance=="object"&&typeof performance.now=="function"){var d=performance;n.unstable_now=function(){return d.now()}}else{var f=Date,m=f.now();n.unstable_now=function(){return f.now()-m}}var x=[],y=[],S=1,j=null,$=3,I=!1,k=!1,A=!1,_=typeof setTimeout=="function"?setTimeout:null,J=typeof clearTimeout=="function"?clearTimeout:null,G=typeof setImmediate<"u"?setImmediate:null;typeof navigator<"u"&&navigator.scheduling!==void 0&&navigator.scheduling.isInputPending!==void 0&&navigator.scheduling.isInputPending.bind(navigator.scheduling);function H(P){for(var F=s(y);F!==null;){if(F.callback===null)l(y);else if(F.startTime<=P)l(y),F.sortIndex=F.expirationTime,i(x,F);else break;F=s(y)}}function X(P){if(A=!1,H(P),!k)if(s(x)!==null)k=!0,b(D);else{var F=s(y);F!==null&&W(X,F.startTime-P)}}function D(P,F){k=!1,A&&(A=!1,J(le),le=-1),I=!0;var B=$;try{for(H(F),j=s(x);j!==null&&(!(j.expirationTime>F)||P&&!pe());){var w=j.callback;if(typeof w=="function"){j.callback=null,$=j.priorityLevel;var L=w(j.expirationTime<=F);F=n.unstable_now(),typeof L=="function"?j.callback=L:j===s(x)&&l(x),H(F)}else l(x);j=s(x)}if(j!==null)var ie=!0;else{var ae=s(y);ae!==null&&W(X,ae.startTime-F),ie=!1}return ie}finally{j=null,$=B,I=!1}}var N=!1,Q=null,le=-1,Se=5,ge=-1;function pe(){return!(n.unstable_now()-ge<Se)}function Be(){if(Q!==null){var P=n.unstable_now();ge=P;var F=!0;try{F=Q(!0,P)}finally{F?Fe():(N=!1,Q=null)}}else N=!1}var Fe;if(typeof G=="function")Fe=function(){G(Be)};else if(typeof MessageChannel<"u"){var V=new MessageChannel,z=V.port2;V.port1.onmessage=Be,Fe=function(){z.postMessage(null)}}else Fe=function(){_(Be,0)};function b(P){Q=P,N||(N=!0,Fe())}function W(P,F){le=_(function(){P(n.unstable_now())},F)}n.unstable_IdlePriority=5,n.unstable_ImmediatePriority=1,n.unstable_LowPriority=4,n.unstable_NormalPriority=3,n.unstable_Profiling=null,n.unstable_UserBlockingPriority=2,n.unstable_cancelCallback=function(P){P.callback=null},n.unstable_continueExecution=function(){k||I||(k=!0,b(D))},n.unstable_forceFrameRate=function(P){0>P||125<P?console.error("forceFrameRate takes a positive int between 0 and 125, forcing frame rates higher than 125 fps is not supported"):Se=0<P?Math.floor(1e3/P):5},n.unstable_getCurrentPriorityLevel=function(){return $},n.unstable_getFirstCallbackNode=function(){return s(x)},n.unstable_next=function(P){switch($){case 1:case 2:case 3:var F=3;break;default:F=$}var B=$;$=F;try{return P()}finally{$=B}},n.unstable_pauseExecution=function(){},n.unstable_requestPaint=function(){},n.unstable_runWithPriority=function(P,F){switch(P){case 1:case 2:case 3:case 4:case 5:break;default:P=3}var B=$;$=P;try{return F()}finally{$=B}},n.unstable_scheduleCallback=function(P,F,B){var w=n.unstable_now();switch(typeof B=="object"&&B!==null?(B=B.delay,B=typeof B=="number"&&0<B?w+B:w):B=w,P){case 1:var L=-1;break;case 2:L=250;break;case 5:L=1073741823;break;case 4:L=1e4;break;default:L=5e3}return L=B+L,P={id:S++,callback:F,priorityLevel:P,startTime:B,expirationTime:L,sortIndex:-1},B>w?(P.sortIndex=B,i(y,P),s(x)===null&&P===s(y)&&(A?(J(le),le=-1):A=!0,W(X,B-w))):(P.sortIndex=L,i(x,P),k||I||(k=!0,b(D))),P},n.unstable_shouldYield=pe,n.unstable_wrapCallback=function(P){var F=$;return function(){var B=$;$=F;try{return P.apply(this,arguments)}finally{$=B}}}}($a)),$a}var Pf;function Xg(){return Pf||(Pf=1,Na.exports=Kg()),Na.exports}/**
 * @license React
 * react-dom.production.min.js
 *
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */var Mf;function Jg(){if(Mf)return ht;Mf=1;var n=gu(),i=Xg();function s(e){for(var t="https://reactjs.org/docs/error-decoder.html?invariant="+e,r=1;r<arguments.length;r++)t+="&args[]="+encodeURIComponent(arguments[r]);return"Minified React error #"+e+"; visit "+t+" for the full message or use the non-minified dev environment for full errors and additional helpful warnings."}var l=new Set,c={};function d(e,t){f(e,t),f(e+"Capture",t)}function f(e,t){for(c[e]=t,e=0;e<t.length;e++)l.add(t[e])}var m=!(typeof window>"u"||typeof window.document>"u"||typeof window.document.createElement>"u"),x=Object.prototype.hasOwnProperty,y=/^[:A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD][:A-Z_a-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02FF\u0370-\u037D\u037F-\u1FFF\u200C-\u200D\u2070-\u218F\u2C00-\u2FEF\u3001-\uD7FF\uF900-\uFDCF\uFDF0-\uFFFD\-.0-9\u00B7\u0300-\u036F\u203F-\u2040]*$/,S={},j={};function $(e){return x.call(j,e)?!0:x.call(S,e)?!1:y.test(e)?j[e]=!0:(S[e]=!0,!1)}function I(e,t,r,o){if(r!==null&&r.type===0)return!1;switch(typeof t){case"function":case"symbol":return!0;case"boolean":return o?!1:r!==null?!r.acceptsBooleans:(e=e.toLowerCase().slice(0,5),e!=="data-"&&e!=="aria-");default:return!1}}function k(e,t,r,o){if(t===null||typeof t>"u"||I(e,t,r,o))return!0;if(o)return!1;if(r!==null)switch(r.type){case 3:return!t;case 4:return t===!1;case 5:return isNaN(t);case 6:return isNaN(t)||1>t}return!1}function A(e,t,r,o,a,u,p){this.acceptsBooleans=t===2||t===3||t===4,this.attributeName=o,this.attributeNamespace=a,this.mustUseProperty=r,this.propertyName=e,this.type=t,this.sanitizeURL=u,this.removeEmptyString=p}var _={};"children dangerouslySetInnerHTML defaultValue defaultChecked innerHTML suppressContentEditableWarning suppressHydrationWarning style".split(" ").forEach(function(e){_[e]=new A(e,0,!1,e,null,!1,!1)}),[["acceptCharset","accept-charset"],["className","class"],["htmlFor","for"],["httpEquiv","http-equiv"]].forEach(function(e){var t=e[0];_[t]=new A(t,1,!1,e[1],null,!1,!1)}),["contentEditable","draggable","spellCheck","value"].forEach(function(e){_[e]=new A(e,2,!1,e.toLowerCase(),null,!1,!1)}),["autoReverse","externalResourcesRequired","focusable","preserveAlpha"].forEach(function(e){_[e]=new A(e,2,!1,e,null,!1,!1)}),"allowFullScreen async autoFocus autoPlay controls default defer disabled disablePictureInPicture disableRemotePlayback formNoValidate hidden loop noModule noValidate open playsInline readOnly required reversed scoped seamless itemScope".split(" ").forEach(function(e){_[e]=new A(e,3,!1,e.toLowerCase(),null,!1,!1)}),["checked","multiple","muted","selected"].forEach(function(e){_[e]=new A(e,3,!0,e,null,!1,!1)}),["capture","download"].forEach(function(e){_[e]=new A(e,4,!1,e,null,!1,!1)}),["cols","rows","size","span"].forEach(function(e){_[e]=new A(e,6,!1,e,null,!1,!1)}),["rowSpan","start"].forEach(function(e){_[e]=new A(e,5,!1,e.toLowerCase(),null,!1,!1)});var J=/[\-:]([a-z])/g;function G(e){return e[1].toUpperCase()}"accent-height alignment-baseline arabic-form baseline-shift cap-height clip-path clip-rule color-interpolation color-interpolation-filters color-profile color-rendering dominant-baseline enable-background fill-opacity fill-rule flood-color flood-opacity font-family font-size font-size-adjust font-stretch font-style font-variant font-weight glyph-name glyph-orientation-horizontal glyph-orientation-vertical horiz-adv-x horiz-origin-x image-rendering letter-spacing lighting-color marker-end marker-mid marker-start overline-position overline-thickness paint-order panose-1 pointer-events rendering-intent shape-rendering stop-color stop-opacity strikethrough-position strikethrough-thickness stroke-dasharray stroke-dashoffset stroke-linecap stroke-linejoin stroke-miterlimit stroke-opacity stroke-width text-anchor text-decoration text-rendering underline-position underline-thickness unicode-bidi unicode-range units-per-em v-alphabetic v-hanging v-ideographic v-mathematical vector-effect vert-adv-y vert-origin-x vert-origin-y word-spacing writing-mode xmlns:xlink x-height".split(" ").forEach(function(e){var t=e.replace(J,G);_[t]=new A(t,1,!1,e,null,!1,!1)}),"xlink:actuate xlink:arcrole xlink:role xlink:show xlink:title xlink:type".split(" ").forEach(function(e){var t=e.replace(J,G);_[t]=new A(t,1,!1,e,"http://www.w3.org/1999/xlink",!1,!1)}),["xml:base","xml:lang","xml:space"].forEach(function(e){var t=e.replace(J,G);_[t]=new A(t,1,!1,e,"http://www.w3.org/XML/1998/namespace",!1,!1)}),["tabIndex","crossOrigin"].forEach(function(e){_[e]=new A(e,1,!1,e.toLowerCase(),null,!1,!1)}),_.xlinkHref=new A("xlinkHref",1,!1,"xlink:href","http://www.w3.org/1999/xlink",!0,!1),["src","href","action","formAction"].forEach(function(e){_[e]=new A(e,1,!1,e.toLowerCase(),null,!0,!0)});function H(e,t,r,o){var a=_.hasOwnProperty(t)?_[t]:null;(a!==null?a.type!==0:o||!(2<t.length)||t[0]!=="o"&&t[0]!=="O"||t[1]!=="n"&&t[1]!=="N")&&(k(t,r,a,o)&&(r=null),o||a===null?$(t)&&(r===null?e.removeAttribute(t):e.setAttribute(t,""+r)):a.mustUseProperty?e[a.propertyName]=r===null?a.type===3?!1:"":r:(t=a.attributeName,o=a.attributeNamespace,r===null?e.removeAttribute(t):(a=a.type,r=a===3||a===4&&r===!0?"":""+r,o?e.setAttributeNS(o,t,r):e.setAttribute(t,r))))}var X=n.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED,D=Symbol.for("react.element"),N=Symbol.for("react.portal"),Q=Symbol.for("react.fragment"),le=Symbol.for("react.strict_mode"),Se=Symbol.for("react.profiler"),ge=Symbol.for("react.provider"),pe=Symbol.for("react.context"),Be=Symbol.for("react.forward_ref"),Fe=Symbol.for("react.suspense"),V=Symbol.for("react.suspense_list"),z=Symbol.for("react.memo"),b=Symbol.for("react.lazy"),W=Symbol.for("react.offscreen"),P=Symbol.iterator;function F(e){return e===null||typeof e!="object"?null:(e=P&&e[P]||e["@@iterator"],typeof e=="function"?e:null)}var B=Object.assign,w;function L(e){if(w===void 0)try{throw Error()}catch(r){var t=r.stack.trim().match(/\n( *(at )?)/);w=t&&t[1]||""}return`
`+w+e}var ie=!1;function ae(e,t){if(!e||ie)return"";ie=!0;var r=Error.prepareStackTrace;Error.prepareStackTrace=void 0;try{if(t)if(t=function(){throw Error()},Object.defineProperty(t.prototype,"props",{set:function(){throw Error()}}),typeof Reflect=="object"&&Reflect.construct){try{Reflect.construct(t,[])}catch(T){var o=T}Reflect.construct(e,[],t)}else{try{t.call()}catch(T){o=T}e.call(t.prototype)}else{try{throw Error()}catch(T){o=T}e()}}catch(T){if(T&&o&&typeof T.stack=="string"){for(var a=T.stack.split(`
`),u=o.stack.split(`
`),p=a.length-1,g=u.length-1;1<=p&&0<=g&&a[p]!==u[g];)g--;for(;1<=p&&0<=g;p--,g--)if(a[p]!==u[g]){if(p!==1||g!==1)do if(p--,g--,0>g||a[p]!==u[g]){var v=`
`+a[p].replace(" at new "," at ");return e.displayName&&v.includes("<anonymous>")&&(v=v.replace("<anonymous>",e.displayName)),v}while(1<=p&&0<=g);break}}}finally{ie=!1,Error.prepareStackTrace=r}return(e=e?e.displayName||e.name:"")?L(e):""}function de(e){switch(e.tag){case 5:return L(e.type);case 16:return L("Lazy");case 13:return L("Suspense");case 19:return L("SuspenseList");case 0:case 2:case 15:return e=ae(e.type,!1),e;case 11:return e=ae(e.type.render,!1),e;case 1:return e=ae(e.type,!0),e;default:return""}}function he(e){if(e==null)return null;if(typeof e=="function")return e.displayName||e.name||null;if(typeof e=="string")return e;switch(e){case Q:return"Fragment";case N:return"Portal";case Se:return"Profiler";case le:return"StrictMode";case Fe:return"Suspense";case V:return"SuspenseList"}if(typeof e=="object")switch(e.$$typeof){case pe:return(e.displayName||"Context")+".Consumer";case ge:return(e._context.displayName||"Context")+".Provider";case Be:var t=e.render;return e=e.displayName,e||(e=t.displayName||t.name||"",e=e!==""?"ForwardRef("+e+")":"ForwardRef"),e;case z:return t=e.displayName||null,t!==null?t:he(e.type)||"Memo";case b:t=e._payload,e=e._init;try{return he(e(t))}catch{}}return null}function we(e){var t=e.type;switch(e.tag){case 24:return"Cache";case 9:return(t.displayName||"Context")+".Consumer";case 10:return(t._context.displayName||"Context")+".Provider";case 18:return"DehydratedFragment";case 11:return e=t.render,e=e.displayName||e.name||"",t.displayName||(e!==""?"ForwardRef("+e+")":"ForwardRef");case 7:return"Fragment";case 5:return t;case 4:return"Portal";case 3:return"Root";case 6:return"Text";case 16:return he(t);case 8:return t===le?"StrictMode":"Mode";case 22:return"Offscreen";case 12:return"Profiler";case 21:return"Scope";case 13:return"Suspense";case 19:return"SuspenseList";case 25:return"TracingMarker";case 1:case 0:case 17:case 2:case 14:case 15:if(typeof t=="function")return t.displayName||t.name||null;if(typeof t=="string")return t}return null}function ye(e){switch(typeof e){case"boolean":case"number":case"string":case"undefined":return e;case"object":return e;default:return""}}function xe(e){var t=e.type;return(e=e.nodeName)&&e.toLowerCase()==="input"&&(t==="checkbox"||t==="radio")}function Ee(e){var t=xe(e)?"checked":"value",r=Object.getOwnPropertyDescriptor(e.constructor.prototype,t),o=""+e[t];if(!e.hasOwnProperty(t)&&typeof r<"u"&&typeof r.get=="function"&&typeof r.set=="function"){var a=r.get,u=r.set;return Object.defineProperty(e,t,{configurable:!0,get:function(){return a.call(this)},set:function(p){o=""+p,u.call(this,p)}}),Object.defineProperty(e,t,{enumerable:r.enumerable}),{getValue:function(){return o},setValue:function(p){o=""+p},stopTracking:function(){e._valueTracker=null,delete e[t]}}}}function We(e){e._valueTracker||(e._valueTracker=Ee(e))}function Xe(e){if(!e)return!1;var t=e._valueTracker;if(!t)return!0;var r=t.getValue(),o="";return e&&(o=xe(e)?e.checked?"true":"false":e.value),e=o,e!==r?(t.setValue(e),!0):!1}function qt(e){if(e=e||(typeof document<"u"?document:void 0),typeof e>"u")return null;try{return e.activeElement||e.body}catch{return e.body}}function Ds(e,t){var r=t.checked;return B({},t,{defaultChecked:void 0,defaultValue:void 0,value:void 0,checked:r??e._wrapperState.initialChecked})}function Pu(e,t){var r=t.defaultValue==null?"":t.defaultValue,o=t.checked!=null?t.checked:t.defaultChecked;r=ye(t.value!=null?t.value:r),e._wrapperState={initialChecked:o,initialValue:r,controlled:t.type==="checkbox"||t.type==="radio"?t.checked!=null:t.value!=null}}function Mu(e,t){t=t.checked,t!=null&&H(e,"checked",t,!1)}function Is(e,t){Mu(e,t);var r=ye(t.value),o=t.type;if(r!=null)o==="number"?(r===0&&e.value===""||e.value!=r)&&(e.value=""+r):e.value!==""+r&&(e.value=""+r);else if(o==="submit"||o==="reset"){e.removeAttribute("value");return}t.hasOwnProperty("value")?bs(e,t.type,r):t.hasOwnProperty("defaultValue")&&bs(e,t.type,ye(t.defaultValue)),t.checked==null&&t.defaultChecked!=null&&(e.defaultChecked=!!t.defaultChecked)}function _u(e,t,r){if(t.hasOwnProperty("value")||t.hasOwnProperty("defaultValue")){var o=t.type;if(!(o!=="submit"&&o!=="reset"||t.value!==void 0&&t.value!==null))return;t=""+e._wrapperState.initialValue,r||t===e.value||(e.value=t),e.defaultValue=t}r=e.name,r!==""&&(e.name=""),e.defaultChecked=!!e._wrapperState.initialChecked,r!==""&&(e.name=r)}function bs(e,t,r){(t!=="number"||qt(e.ownerDocument)!==e)&&(r==null?e.defaultValue=""+e._wrapperState.initialValue:e.defaultValue!==""+r&&(e.defaultValue=""+r))}var Ir=Array.isArray;function Xn(e,t,r,o){if(e=e.options,t){t={};for(var a=0;a<r.length;a++)t["$"+r[a]]=!0;for(r=0;r<e.length;r++)a=t.hasOwnProperty("$"+e[r].value),e[r].selected!==a&&(e[r].selected=a),a&&o&&(e[r].defaultSelected=!0)}else{for(r=""+ye(r),t=null,a=0;a<e.length;a++){if(e[a].value===r){e[a].selected=!0,o&&(e[a].defaultSelected=!0);return}t!==null||e[a].disabled||(t=e[a])}t!==null&&(t.selected=!0)}}function zs(e,t){if(t.dangerouslySetInnerHTML!=null)throw Error(s(91));return B({},t,{value:void 0,defaultValue:void 0,children:""+e._wrapperState.initialValue})}function Tu(e,t){var r=t.value;if(r==null){if(r=t.children,t=t.defaultValue,r!=null){if(t!=null)throw Error(s(92));if(Ir(r)){if(1<r.length)throw Error(s(93));r=r[0]}t=r}t==null&&(t=""),r=t}e._wrapperState={initialValue:ye(r)}}function Nu(e,t){var r=ye(t.value),o=ye(t.defaultValue);r!=null&&(r=""+r,r!==e.value&&(e.value=r),t.defaultValue==null&&e.defaultValue!==r&&(e.defaultValue=r)),o!=null&&(e.defaultValue=""+o)}function $u(e){var t=e.textContent;t===e._wrapperState.initialValue&&t!==""&&t!==null&&(e.value=t)}function Ou(e){switch(e){case"svg":return"http://www.w3.org/2000/svg";case"math":return"http://www.w3.org/1998/Math/MathML";default:return"http://www.w3.org/1999/xhtml"}}function Bs(e,t){return e==null||e==="http://www.w3.org/1999/xhtml"?Ou(t):e==="http://www.w3.org/2000/svg"&&t==="foreignObject"?"http://www.w3.org/1999/xhtml":e}var Uo,Lu=function(e){return typeof MSApp<"u"&&MSApp.execUnsafeLocalFunction?function(t,r,o,a){MSApp.execUnsafeLocalFunction(function(){return e(t,r,o,a)})}:e}(function(e,t){if(e.namespaceURI!=="http://www.w3.org/2000/svg"||"innerHTML"in e)e.innerHTML=t;else{for(Uo=Uo||document.createElement("div"),Uo.innerHTML="<svg>"+t.valueOf().toString()+"</svg>",t=Uo.firstChild;e.firstChild;)e.removeChild(e.firstChild);for(;t.firstChild;)e.appendChild(t.firstChild)}});function br(e,t){if(t){var r=e.firstChild;if(r&&r===e.lastChild&&r.nodeType===3){r.nodeValue=t;return}}e.textContent=t}var zr={animationIterationCount:!0,aspectRatio:!0,borderImageOutset:!0,borderImageSlice:!0,borderImageWidth:!0,boxFlex:!0,boxFlexGroup:!0,boxOrdinalGroup:!0,columnCount:!0,columns:!0,flex:!0,flexGrow:!0,flexPositive:!0,flexShrink:!0,flexNegative:!0,flexOrder:!0,gridArea:!0,gridRow:!0,gridRowEnd:!0,gridRowSpan:!0,gridRowStart:!0,gridColumn:!0,gridColumnEnd:!0,gridColumnSpan:!0,gridColumnStart:!0,fontWeight:!0,lineClamp:!0,lineHeight:!0,opacity:!0,order:!0,orphans:!0,tabSize:!0,widows:!0,zIndex:!0,zoom:!0,fillOpacity:!0,floodOpacity:!0,stopOpacity:!0,strokeDasharray:!0,strokeDashoffset:!0,strokeMiterlimit:!0,strokeOpacity:!0,strokeWidth:!0},Kh=["Webkit","ms","Moz","O"];Object.keys(zr).forEach(function(e){Kh.forEach(function(t){t=t+e.charAt(0).toUpperCase()+e.substring(1),zr[t]=zr[e]})});function Du(e,t,r){return t==null||typeof t=="boolean"||t===""?"":r||typeof t!="number"||t===0||zr.hasOwnProperty(e)&&zr[e]?(""+t).trim():t+"px"}function Iu(e,t){e=e.style;for(var r in t)if(t.hasOwnProperty(r)){var o=r.indexOf("--")===0,a=Du(r,t[r],o);r==="float"&&(r="cssFloat"),o?e.setProperty(r,a):e[r]=a}}var Xh=B({menuitem:!0},{area:!0,base:!0,br:!0,col:!0,embed:!0,hr:!0,img:!0,input:!0,keygen:!0,link:!0,meta:!0,param:!0,source:!0,track:!0,wbr:!0});function Fs(e,t){if(t){if(Xh[e]&&(t.children!=null||t.dangerouslySetInnerHTML!=null))throw Error(s(137,e));if(t.dangerouslySetInnerHTML!=null){if(t.children!=null)throw Error(s(60));if(typeof t.dangerouslySetInnerHTML!="object"||!("__html"in t.dangerouslySetInnerHTML))throw Error(s(61))}if(t.style!=null&&typeof t.style!="object")throw Error(s(62))}}function Us(e,t){if(e.indexOf("-")===-1)return typeof t.is=="string";switch(e){case"annotation-xml":case"color-profile":case"font-face":case"font-face-src":case"font-face-uri":case"font-face-format":case"font-face-name":case"missing-glyph":return!1;default:return!0}}var Hs=null;function Ys(e){return e=e.target||e.srcElement||window,e.correspondingUseElement&&(e=e.correspondingUseElement),e.nodeType===3?e.parentNode:e}var Vs=null,Jn=null,Zn=null;function bu(e){if(e=lo(e)){if(typeof Vs!="function")throw Error(s(280));var t=e.stateNode;t&&(t=di(t),Vs(e.stateNode,e.type,t))}}function zu(e){Jn?Zn?Zn.push(e):Zn=[e]:Jn=e}function Bu(){if(Jn){var e=Jn,t=Zn;if(Zn=Jn=null,bu(e),t)for(e=0;e<t.length;e++)bu(t[e])}}function Fu(e,t){return e(t)}function Uu(){}var Ws=!1;function Hu(e,t,r){if(Ws)return e(t,r);Ws=!0;try{return Fu(e,t,r)}finally{Ws=!1,(Jn!==null||Zn!==null)&&(Uu(),Bu())}}function Br(e,t){var r=e.stateNode;if(r===null)return null;var o=di(r);if(o===null)return null;r=o[t];e:switch(t){case"onClick":case"onClickCapture":case"onDoubleClick":case"onDoubleClickCapture":case"onMouseDown":case"onMouseDownCapture":case"onMouseMove":case"onMouseMoveCapture":case"onMouseUp":case"onMouseUpCapture":case"onMouseEnter":(o=!o.disabled)||(e=e.type,o=!(e==="button"||e==="input"||e==="select"||e==="textarea")),e=!o;break e;default:e=!1}if(e)return null;if(r&&typeof r!="function")throw Error(s(231,t,typeof r));return r}var qs=!1;if(m)try{var Fr={};Object.defineProperty(Fr,"passive",{get:function(){qs=!0}}),window.addEventListener("test",Fr,Fr),window.removeEventListener("test",Fr,Fr)}catch{qs=!1}function Jh(e,t,r,o,a,u,p,g,v){var T=Array.prototype.slice.call(arguments,3);try{t.apply(r,T)}catch(Y){this.onError(Y)}}var Ur=!1,Ho=null,Yo=!1,Qs=null,Zh={onError:function(e){Ur=!0,Ho=e}};function em(e,t,r,o,a,u,p,g,v){Ur=!1,Ho=null,Jh.apply(Zh,arguments)}function tm(e,t,r,o,a,u,p,g,v){if(em.apply(this,arguments),Ur){if(Ur){var T=Ho;Ur=!1,Ho=null}else throw Error(s(198));Yo||(Yo=!0,Qs=T)}}function Pn(e){var t=e,r=e;if(e.alternate)for(;t.return;)t=t.return;else{e=t;do t=e,t.flags&4098&&(r=t.return),e=t.return;while(e)}return t.tag===3?r:null}function Yu(e){if(e.tag===13){var t=e.memoizedState;if(t===null&&(e=e.alternate,e!==null&&(t=e.memoizedState)),t!==null)return t.dehydrated}return null}function Vu(e){if(Pn(e)!==e)throw Error(s(188))}function nm(e){var t=e.alternate;if(!t){if(t=Pn(e),t===null)throw Error(s(188));return t!==e?null:e}for(var r=e,o=t;;){var a=r.return;if(a===null)break;var u=a.alternate;if(u===null){if(o=a.return,o!==null){r=o;continue}break}if(a.child===u.child){for(u=a.child;u;){if(u===r)return Vu(a),e;if(u===o)return Vu(a),t;u=u.sibling}throw Error(s(188))}if(r.return!==o.return)r=a,o=u;else{for(var p=!1,g=a.child;g;){if(g===r){p=!0,r=a,o=u;break}if(g===o){p=!0,o=a,r=u;break}g=g.sibling}if(!p){for(g=u.child;g;){if(g===r){p=!0,r=u,o=a;break}if(g===o){p=!0,o=u,r=a;break}g=g.sibling}if(!p)throw Error(s(189))}}if(r.alternate!==o)throw Error(s(190))}if(r.tag!==3)throw Error(s(188));return r.stateNode.current===r?e:t}function Wu(e){return e=nm(e),e!==null?qu(e):null}function qu(e){if(e.tag===5||e.tag===6)return e;for(e=e.child;e!==null;){var t=qu(e);if(t!==null)return t;e=e.sibling}return null}var Qu=i.unstable_scheduleCallback,Gu=i.unstable_cancelCallback,rm=i.unstable_shouldYield,om=i.unstable_requestPaint,Ie=i.unstable_now,im=i.unstable_getCurrentPriorityLevel,Gs=i.unstable_ImmediatePriority,Ku=i.unstable_UserBlockingPriority,Vo=i.unstable_NormalPriority,sm=i.unstable_LowPriority,Xu=i.unstable_IdlePriority,Wo=null,Bt=null;function lm(e){if(Bt&&typeof Bt.onCommitFiberRoot=="function")try{Bt.onCommitFiberRoot(Wo,e,void 0,(e.current.flags&128)===128)}catch{}}var Tt=Math.clz32?Math.clz32:cm,am=Math.log,um=Math.LN2;function cm(e){return e>>>=0,e===0?32:31-(am(e)/um|0)|0}var qo=64,Qo=4194304;function Hr(e){switch(e&-e){case 1:return 1;case 2:return 2;case 4:return 4;case 8:return 8;case 16:return 16;case 32:return 32;case 64:case 128:case 256:case 512:case 1024:case 2048:case 4096:case 8192:case 16384:case 32768:case 65536:case 131072:case 262144:case 524288:case 1048576:case 2097152:return e&4194240;case 4194304:case 8388608:case 16777216:case 33554432:case 67108864:return e&130023424;case 134217728:return 134217728;case 268435456:return 268435456;case 536870912:return 536870912;case 1073741824:return 1073741824;default:return e}}function Go(e,t){var r=e.pendingLanes;if(r===0)return 0;var o=0,a=e.suspendedLanes,u=e.pingedLanes,p=r&268435455;if(p!==0){var g=p&~a;g!==0?o=Hr(g):(u&=p,u!==0&&(o=Hr(u)))}else p=r&~a,p!==0?o=Hr(p):u!==0&&(o=Hr(u));if(o===0)return 0;if(t!==0&&t!==o&&!(t&a)&&(a=o&-o,u=t&-t,a>=u||a===16&&(u&4194240)!==0))return t;if(o&4&&(o|=r&16),t=e.entangledLanes,t!==0)for(e=e.entanglements,t&=o;0<t;)r=31-Tt(t),a=1<<r,o|=e[r],t&=~a;return o}function dm(e,t){switch(e){case 1:case 2:case 4:return t+250;case 8:case 16:case 32:case 64:case 128:case 256:case 512:case 1024:case 2048:case 4096:case 8192:case 16384:case 32768:case 65536:case 131072:case 262144:case 524288:case 1048576:case 2097152:return t+5e3;case 4194304:case 8388608:case 16777216:case 33554432:case 67108864:return-1;case 134217728:case 268435456:case 536870912:case 1073741824:return-1;default:return-1}}function fm(e,t){for(var r=e.suspendedLanes,o=e.pingedLanes,a=e.expirationTimes,u=e.pendingLanes;0<u;){var p=31-Tt(u),g=1<<p,v=a[p];v===-1?(!(g&r)||g&o)&&(a[p]=dm(g,t)):v<=t&&(e.expiredLanes|=g),u&=~g}}function Ks(e){return e=e.pendingLanes&-1073741825,e!==0?e:e&1073741824?1073741824:0}function Ju(){var e=qo;return qo<<=1,!(qo&4194240)&&(qo=64),e}function Xs(e){for(var t=[],r=0;31>r;r++)t.push(e);return t}function Yr(e,t,r){e.pendingLanes|=t,t!==536870912&&(e.suspendedLanes=0,e.pingedLanes=0),e=e.eventTimes,t=31-Tt(t),e[t]=r}function pm(e,t){var r=e.pendingLanes&~t;e.pendingLanes=t,e.suspendedLanes=0,e.pingedLanes=0,e.expiredLanes&=t,e.mutableReadLanes&=t,e.entangledLanes&=t,t=e.entanglements;var o=e.eventTimes;for(e=e.expirationTimes;0<r;){var a=31-Tt(r),u=1<<a;t[a]=0,o[a]=-1,e[a]=-1,r&=~u}}function Js(e,t){var r=e.entangledLanes|=t;for(e=e.entanglements;r;){var o=31-Tt(r),a=1<<o;a&t|e[o]&t&&(e[o]|=t),r&=~a}}var Re=0;function Zu(e){return e&=-e,1<e?4<e?e&268435455?16:536870912:4:1}var ec,Zs,tc,nc,rc,el=!1,Ko=[],on=null,sn=null,ln=null,Vr=new Map,Wr=new Map,an=[],hm="mousedown mouseup touchcancel touchend touchstart auxclick dblclick pointercancel pointerdown pointerup dragend dragstart drop compositionend compositionstart keydown keypress keyup input textInput copy cut paste click change contextmenu reset submit".split(" ");function oc(e,t){switch(e){case"focusin":case"focusout":on=null;break;case"dragenter":case"dragleave":sn=null;break;case"mouseover":case"mouseout":ln=null;break;case"pointerover":case"pointerout":Vr.delete(t.pointerId);break;case"gotpointercapture":case"lostpointercapture":Wr.delete(t.pointerId)}}function qr(e,t,r,o,a,u){return e===null||e.nativeEvent!==u?(e={blockedOn:t,domEventName:r,eventSystemFlags:o,nativeEvent:u,targetContainers:[a]},t!==null&&(t=lo(t),t!==null&&Zs(t)),e):(e.eventSystemFlags|=o,t=e.targetContainers,a!==null&&t.indexOf(a)===-1&&t.push(a),e)}function mm(e,t,r,o,a){switch(t){case"focusin":return on=qr(on,e,t,r,o,a),!0;case"dragenter":return sn=qr(sn,e,t,r,o,a),!0;case"mouseover":return ln=qr(ln,e,t,r,o,a),!0;case"pointerover":var u=a.pointerId;return Vr.set(u,qr(Vr.get(u)||null,e,t,r,o,a)),!0;case"gotpointercapture":return u=a.pointerId,Wr.set(u,qr(Wr.get(u)||null,e,t,r,o,a)),!0}return!1}function ic(e){var t=Mn(e.target);if(t!==null){var r=Pn(t);if(r!==null){if(t=r.tag,t===13){if(t=Yu(r),t!==null){e.blockedOn=t,rc(e.priority,function(){tc(r)});return}}else if(t===3&&r.stateNode.current.memoizedState.isDehydrated){e.blockedOn=r.tag===3?r.stateNode.containerInfo:null;return}}}e.blockedOn=null}function Xo(e){if(e.blockedOn!==null)return!1;for(var t=e.targetContainers;0<t.length;){var r=nl(e.domEventName,e.eventSystemFlags,t[0],e.nativeEvent);if(r===null){r=e.nativeEvent;var o=new r.constructor(r.type,r);Hs=o,r.target.dispatchEvent(o),Hs=null}else return t=lo(r),t!==null&&Zs(t),e.blockedOn=r,!1;t.shift()}return!0}function sc(e,t,r){Xo(e)&&r.delete(t)}function gm(){el=!1,on!==null&&Xo(on)&&(on=null),sn!==null&&Xo(sn)&&(sn=null),ln!==null&&Xo(ln)&&(ln=null),Vr.forEach(sc),Wr.forEach(sc)}function Qr(e,t){e.blockedOn===t&&(e.blockedOn=null,el||(el=!0,i.unstable_scheduleCallback(i.unstable_NormalPriority,gm)))}function Gr(e){function t(a){return Qr(a,e)}if(0<Ko.length){Qr(Ko[0],e);for(var r=1;r<Ko.length;r++){var o=Ko[r];o.blockedOn===e&&(o.blockedOn=null)}}for(on!==null&&Qr(on,e),sn!==null&&Qr(sn,e),ln!==null&&Qr(ln,e),Vr.forEach(t),Wr.forEach(t),r=0;r<an.length;r++)o=an[r],o.blockedOn===e&&(o.blockedOn=null);for(;0<an.length&&(r=an[0],r.blockedOn===null);)ic(r),r.blockedOn===null&&an.shift()}var er=X.ReactCurrentBatchConfig,Jo=!0;function ym(e,t,r,o){var a=Re,u=er.transition;er.transition=null;try{Re=1,tl(e,t,r,o)}finally{Re=a,er.transition=u}}function xm(e,t,r,o){var a=Re,u=er.transition;er.transition=null;try{Re=4,tl(e,t,r,o)}finally{Re=a,er.transition=u}}function tl(e,t,r,o){if(Jo){var a=nl(e,t,r,o);if(a===null)vl(e,t,o,Zo,r),oc(e,o);else if(mm(a,e,t,r,o))o.stopPropagation();else if(oc(e,o),t&4&&-1<hm.indexOf(e)){for(;a!==null;){var u=lo(a);if(u!==null&&ec(u),u=nl(e,t,r,o),u===null&&vl(e,t,o,Zo,r),u===a)break;a=u}a!==null&&o.stopPropagation()}else vl(e,t,o,null,r)}}var Zo=null;function nl(e,t,r,o){if(Zo=null,e=Ys(o),e=Mn(e),e!==null)if(t=Pn(e),t===null)e=null;else if(r=t.tag,r===13){if(e=Yu(t),e!==null)return e;e=null}else if(r===3){if(t.stateNode.current.memoizedState.isDehydrated)return t.tag===3?t.stateNode.containerInfo:null;e=null}else t!==e&&(e=null);return Zo=e,null}function lc(e){switch(e){case"cancel":case"click":case"close":case"contextmenu":case"copy":case"cut":case"auxclick":case"dblclick":case"dragend":case"dragstart":case"drop":case"focusin":case"focusout":case"input":case"invalid":case"keydown":case"keypress":case"keyup":case"mousedown":case"mouseup":case"paste":case"pause":case"play":case"pointercancel":case"pointerdown":case"pointerup":case"ratechange":case"reset":case"resize":case"seeked":case"submit":case"touchcancel":case"touchend":case"touchstart":case"volumechange":case"change":case"selectionchange":case"textInput":case"compositionstart":case"compositionend":case"compositionupdate":case"beforeblur":case"afterblur":case"beforeinput":case"blur":case"fullscreenchange":case"focus":case"hashchange":case"popstate":case"select":case"selectstart":return 1;case"drag":case"dragenter":case"dragexit":case"dragleave":case"dragover":case"mousemove":case"mouseout":case"mouseover":case"pointermove":case"pointerout":case"pointerover":case"scroll":case"toggle":case"touchmove":case"wheel":case"mouseenter":case"mouseleave":case"pointerenter":case"pointerleave":return 4;case"message":switch(im()){case Gs:return 1;case Ku:return 4;case Vo:case sm:return 16;case Xu:return 536870912;default:return 16}default:return 16}}var un=null,rl=null,ei=null;function ac(){if(ei)return ei;var e,t=rl,r=t.length,o,a="value"in un?un.value:un.textContent,u=a.length;for(e=0;e<r&&t[e]===a[e];e++);var p=r-e;for(o=1;o<=p&&t[r-o]===a[u-o];o++);return ei=a.slice(e,1<o?1-o:void 0)}function ti(e){var t=e.keyCode;return"charCode"in e?(e=e.charCode,e===0&&t===13&&(e=13)):e=t,e===10&&(e=13),32<=e||e===13?e:0}function ni(){return!0}function uc(){return!1}function gt(e){function t(r,o,a,u,p){this._reactName=r,this._targetInst=a,this.type=o,this.nativeEvent=u,this.target=p,this.currentTarget=null;for(var g in e)e.hasOwnProperty(g)&&(r=e[g],this[g]=r?r(u):u[g]);return this.isDefaultPrevented=(u.defaultPrevented!=null?u.defaultPrevented:u.returnValue===!1)?ni:uc,this.isPropagationStopped=uc,this}return B(t.prototype,{preventDefault:function(){this.defaultPrevented=!0;var r=this.nativeEvent;r&&(r.preventDefault?r.preventDefault():typeof r.returnValue!="unknown"&&(r.returnValue=!1),this.isDefaultPrevented=ni)},stopPropagation:function(){var r=this.nativeEvent;r&&(r.stopPropagation?r.stopPropagation():typeof r.cancelBubble!="unknown"&&(r.cancelBubble=!0),this.isPropagationStopped=ni)},persist:function(){},isPersistent:ni}),t}var tr={eventPhase:0,bubbles:0,cancelable:0,timeStamp:function(e){return e.timeStamp||Date.now()},defaultPrevented:0,isTrusted:0},ol=gt(tr),Kr=B({},tr,{view:0,detail:0}),vm=gt(Kr),il,sl,Xr,ri=B({},Kr,{screenX:0,screenY:0,clientX:0,clientY:0,pageX:0,pageY:0,ctrlKey:0,shiftKey:0,altKey:0,metaKey:0,getModifierState:al,button:0,buttons:0,relatedTarget:function(e){return e.relatedTarget===void 0?e.fromElement===e.srcElement?e.toElement:e.fromElement:e.relatedTarget},movementX:function(e){return"movementX"in e?e.movementX:(e!==Xr&&(Xr&&e.type==="mousemove"?(il=e.screenX-Xr.screenX,sl=e.screenY-Xr.screenY):sl=il=0,Xr=e),il)},movementY:function(e){return"movementY"in e?e.movementY:sl}}),cc=gt(ri),wm=B({},ri,{dataTransfer:0}),Sm=gt(wm),km=B({},Kr,{relatedTarget:0}),ll=gt(km),Cm=B({},tr,{animationName:0,elapsedTime:0,pseudoElement:0}),Em=gt(Cm),jm=B({},tr,{clipboardData:function(e){return"clipboardData"in e?e.clipboardData:window.clipboardData}}),Am=gt(jm),Rm=B({},tr,{data:0}),dc=gt(Rm),Pm={Esc:"Escape",Spacebar:" ",Left:"ArrowLeft",Up:"ArrowUp",Right:"ArrowRight",Down:"ArrowDown",Del:"Delete",Win:"OS",Menu:"ContextMenu",Apps:"ContextMenu",Scroll:"ScrollLock",MozPrintableKey:"Unidentified"},Mm={8:"Backspace",9:"Tab",12:"Clear",13:"Enter",16:"Shift",17:"Control",18:"Alt",19:"Pause",20:"CapsLock",27:"Escape",32:" ",33:"PageUp",34:"PageDown",35:"End",36:"Home",37:"ArrowLeft",38:"ArrowUp",39:"ArrowRight",40:"ArrowDown",45:"Insert",46:"Delete",112:"F1",113:"F2",114:"F3",115:"F4",116:"F5",117:"F6",118:"F7",119:"F8",120:"F9",121:"F10",122:"F11",123:"F12",144:"NumLock",145:"ScrollLock",224:"Meta"},_m={Alt:"altKey",Control:"ctrlKey",Meta:"metaKey",Shift:"shiftKey"};function Tm(e){var t=this.nativeEvent;return t.getModifierState?t.getModifierState(e):(e=_m[e])?!!t[e]:!1}function al(){return Tm}var Nm=B({},Kr,{key:function(e){if(e.key){var t=Pm[e.key]||e.key;if(t!=="Unidentified")return t}return e.type==="keypress"?(e=ti(e),e===13?"Enter":String.fromCharCode(e)):e.type==="keydown"||e.type==="keyup"?Mm[e.keyCode]||"Unidentified":""},code:0,location:0,ctrlKey:0,shiftKey:0,altKey:0,metaKey:0,repeat:0,locale:0,getModifierState:al,charCode:function(e){return e.type==="keypress"?ti(e):0},keyCode:function(e){return e.type==="keydown"||e.type==="keyup"?e.keyCode:0},which:function(e){return e.type==="keypress"?ti(e):e.type==="keydown"||e.type==="keyup"?e.keyCode:0}}),$m=gt(Nm),Om=B({},ri,{pointerId:0,width:0,height:0,pressure:0,tangentialPressure:0,tiltX:0,tiltY:0,twist:0,pointerType:0,isPrimary:0}),fc=gt(Om),Lm=B({},Kr,{touches:0,targetTouches:0,changedTouches:0,altKey:0,metaKey:0,ctrlKey:0,shiftKey:0,getModifierState:al}),Dm=gt(Lm),Im=B({},tr,{propertyName:0,elapsedTime:0,pseudoElement:0}),bm=gt(Im),zm=B({},ri,{deltaX:function(e){return"deltaX"in e?e.deltaX:"wheelDeltaX"in e?-e.wheelDeltaX:0},deltaY:function(e){return"deltaY"in e?e.deltaY:"wheelDeltaY"in e?-e.wheelDeltaY:"wheelDelta"in e?-e.wheelDelta:0},deltaZ:0,deltaMode:0}),Bm=gt(zm),Fm=[9,13,27,32],ul=m&&"CompositionEvent"in window,Jr=null;m&&"documentMode"in document&&(Jr=document.documentMode);var Um=m&&"TextEvent"in window&&!Jr,pc=m&&(!ul||Jr&&8<Jr&&11>=Jr),hc=" ",mc=!1;function gc(e,t){switch(e){case"keyup":return Fm.indexOf(t.keyCode)!==-1;case"keydown":return t.keyCode!==229;case"keypress":case"mousedown":case"focusout":return!0;default:return!1}}function yc(e){return e=e.detail,typeof e=="object"&&"data"in e?e.data:null}var nr=!1;function Hm(e,t){switch(e){case"compositionend":return yc(t);case"keypress":return t.which!==32?null:(mc=!0,hc);case"textInput":return e=t.data,e===hc&&mc?null:e;default:return null}}function Ym(e,t){if(nr)return e==="compositionend"||!ul&&gc(e,t)?(e=ac(),ei=rl=un=null,nr=!1,e):null;switch(e){case"paste":return null;case"keypress":if(!(t.ctrlKey||t.altKey||t.metaKey)||t.ctrlKey&&t.altKey){if(t.char&&1<t.char.length)return t.char;if(t.which)return String.fromCharCode(t.which)}return null;case"compositionend":return pc&&t.locale!=="ko"?null:t.data;default:return null}}var Vm={color:!0,date:!0,datetime:!0,"datetime-local":!0,email:!0,month:!0,number:!0,password:!0,range:!0,search:!0,tel:!0,text:!0,time:!0,url:!0,week:!0};function xc(e){var t=e&&e.nodeName&&e.nodeName.toLowerCase();return t==="input"?!!Vm[e.type]:t==="textarea"}function vc(e,t,r,o){zu(o),t=ai(t,"onChange"),0<t.length&&(r=new ol("onChange","change",null,r,o),e.push({event:r,listeners:t}))}var Zr=null,eo=null;function Wm(e){Ic(e,0)}function oi(e){var t=lr(e);if(Xe(t))return e}function qm(e,t){if(e==="change")return t}var wc=!1;if(m){var cl;if(m){var dl="oninput"in document;if(!dl){var Sc=document.createElement("div");Sc.setAttribute("oninput","return;"),dl=typeof Sc.oninput=="function"}cl=dl}else cl=!1;wc=cl&&(!document.documentMode||9<document.documentMode)}function kc(){Zr&&(Zr.detachEvent("onpropertychange",Cc),eo=Zr=null)}function Cc(e){if(e.propertyName==="value"&&oi(eo)){var t=[];vc(t,eo,e,Ys(e)),Hu(Wm,t)}}function Qm(e,t,r){e==="focusin"?(kc(),Zr=t,eo=r,Zr.attachEvent("onpropertychange",Cc)):e==="focusout"&&kc()}function Gm(e){if(e==="selectionchange"||e==="keyup"||e==="keydown")return oi(eo)}function Km(e,t){if(e==="click")return oi(t)}function Xm(e,t){if(e==="input"||e==="change")return oi(t)}function Jm(e,t){return e===t&&(e!==0||1/e===1/t)||e!==e&&t!==t}var Nt=typeof Object.is=="function"?Object.is:Jm;function to(e,t){if(Nt(e,t))return!0;if(typeof e!="object"||e===null||typeof t!="object"||t===null)return!1;var r=Object.keys(e),o=Object.keys(t);if(r.length!==o.length)return!1;for(o=0;o<r.length;o++){var a=r[o];if(!x.call(t,a)||!Nt(e[a],t[a]))return!1}return!0}function Ec(e){for(;e&&e.firstChild;)e=e.firstChild;return e}function jc(e,t){var r=Ec(e);e=0;for(var o;r;){if(r.nodeType===3){if(o=e+r.textContent.length,e<=t&&o>=t)return{node:r,offset:t-e};e=o}e:{for(;r;){if(r.nextSibling){r=r.nextSibling;break e}r=r.parentNode}r=void 0}r=Ec(r)}}function Ac(e,t){return e&&t?e===t?!0:e&&e.nodeType===3?!1:t&&t.nodeType===3?Ac(e,t.parentNode):"contains"in e?e.contains(t):e.compareDocumentPosition?!!(e.compareDocumentPosition(t)&16):!1:!1}function Rc(){for(var e=window,t=qt();t instanceof e.HTMLIFrameElement;){try{var r=typeof t.contentWindow.location.href=="string"}catch{r=!1}if(r)e=t.contentWindow;else break;t=qt(e.document)}return t}function fl(e){var t=e&&e.nodeName&&e.nodeName.toLowerCase();return t&&(t==="input"&&(e.type==="text"||e.type==="search"||e.type==="tel"||e.type==="url"||e.type==="password")||t==="textarea"||e.contentEditable==="true")}function Zm(e){var t=Rc(),r=e.focusedElem,o=e.selectionRange;if(t!==r&&r&&r.ownerDocument&&Ac(r.ownerDocument.documentElement,r)){if(o!==null&&fl(r)){if(t=o.start,e=o.end,e===void 0&&(e=t),"selectionStart"in r)r.selectionStart=t,r.selectionEnd=Math.min(e,r.value.length);else if(e=(t=r.ownerDocument||document)&&t.defaultView||window,e.getSelection){e=e.getSelection();var a=r.textContent.length,u=Math.min(o.start,a);o=o.end===void 0?u:Math.min(o.end,a),!e.extend&&u>o&&(a=o,o=u,u=a),a=jc(r,u);var p=jc(r,o);a&&p&&(e.rangeCount!==1||e.anchorNode!==a.node||e.anchorOffset!==a.offset||e.focusNode!==p.node||e.focusOffset!==p.offset)&&(t=t.createRange(),t.setStart(a.node,a.offset),e.removeAllRanges(),u>o?(e.addRange(t),e.extend(p.node,p.offset)):(t.setEnd(p.node,p.offset),e.addRange(t)))}}for(t=[],e=r;e=e.parentNode;)e.nodeType===1&&t.push({element:e,left:e.scrollLeft,top:e.scrollTop});for(typeof r.focus=="function"&&r.focus(),r=0;r<t.length;r++)e=t[r],e.element.scrollLeft=e.left,e.element.scrollTop=e.top}}var eg=m&&"documentMode"in document&&11>=document.documentMode,rr=null,pl=null,no=null,hl=!1;function Pc(e,t,r){var o=r.window===r?r.document:r.nodeType===9?r:r.ownerDocument;hl||rr==null||rr!==qt(o)||(o=rr,"selectionStart"in o&&fl(o)?o={start:o.selectionStart,end:o.selectionEnd}:(o=(o.ownerDocument&&o.ownerDocument.defaultView||window).getSelection(),o={anchorNode:o.anchorNode,anchorOffset:o.anchorOffset,focusNode:o.focusNode,focusOffset:o.focusOffset}),no&&to(no,o)||(no=o,o=ai(pl,"onSelect"),0<o.length&&(t=new ol("onSelect","select",null,t,r),e.push({event:t,listeners:o}),t.target=rr)))}function ii(e,t){var r={};return r[e.toLowerCase()]=t.toLowerCase(),r["Webkit"+e]="webkit"+t,r["Moz"+e]="moz"+t,r}var or={animationend:ii("Animation","AnimationEnd"),animationiteration:ii("Animation","AnimationIteration"),animationstart:ii("Animation","AnimationStart"),transitionend:ii("Transition","TransitionEnd")},ml={},Mc={};m&&(Mc=document.createElement("div").style,"AnimationEvent"in window||(delete or.animationend.animation,delete or.animationiteration.animation,delete or.animationstart.animation),"TransitionEvent"in window||delete or.transitionend.transition);function si(e){if(ml[e])return ml[e];if(!or[e])return e;var t=or[e],r;for(r in t)if(t.hasOwnProperty(r)&&r in Mc)return ml[e]=t[r];return e}var _c=si("animationend"),Tc=si("animationiteration"),Nc=si("animationstart"),$c=si("transitionend"),Oc=new Map,Lc="abort auxClick cancel canPlay canPlayThrough click close contextMenu copy cut drag dragEnd dragEnter dragExit dragLeave dragOver dragStart drop durationChange emptied encrypted ended error gotPointerCapture input invalid keyDown keyPress keyUp load loadedData loadedMetadata loadStart lostPointerCapture mouseDown mouseMove mouseOut mouseOver mouseUp paste pause play playing pointerCancel pointerDown pointerMove pointerOut pointerOver pointerUp progress rateChange reset resize seeked seeking stalled submit suspend timeUpdate touchCancel touchEnd touchStart volumeChange scroll toggle touchMove waiting wheel".split(" ");function cn(e,t){Oc.set(e,t),d(t,[e])}for(var gl=0;gl<Lc.length;gl++){var yl=Lc[gl],tg=yl.toLowerCase(),ng=yl[0].toUpperCase()+yl.slice(1);cn(tg,"on"+ng)}cn(_c,"onAnimationEnd"),cn(Tc,"onAnimationIteration"),cn(Nc,"onAnimationStart"),cn("dblclick","onDoubleClick"),cn("focusin","onFocus"),cn("focusout","onBlur"),cn($c,"onTransitionEnd"),f("onMouseEnter",["mouseout","mouseover"]),f("onMouseLeave",["mouseout","mouseover"]),f("onPointerEnter",["pointerout","pointerover"]),f("onPointerLeave",["pointerout","pointerover"]),d("onChange","change click focusin focusout input keydown keyup selectionchange".split(" ")),d("onSelect","focusout contextmenu dragend focusin keydown keyup mousedown mouseup selectionchange".split(" ")),d("onBeforeInput",["compositionend","keypress","textInput","paste"]),d("onCompositionEnd","compositionend focusout keydown keypress keyup mousedown".split(" ")),d("onCompositionStart","compositionstart focusout keydown keypress keyup mousedown".split(" ")),d("onCompositionUpdate","compositionupdate focusout keydown keypress keyup mousedown".split(" "));var ro="abort canplay canplaythrough durationchange emptied encrypted ended error loadeddata loadedmetadata loadstart pause play playing progress ratechange resize seeked seeking stalled suspend timeupdate volumechange waiting".split(" "),rg=new Set("cancel close invalid load scroll toggle".split(" ").concat(ro));function Dc(e,t,r){var o=e.type||"unknown-event";e.currentTarget=r,tm(o,t,void 0,e),e.currentTarget=null}function Ic(e,t){t=(t&4)!==0;for(var r=0;r<e.length;r++){var o=e[r],a=o.event;o=o.listeners;e:{var u=void 0;if(t)for(var p=o.length-1;0<=p;p--){var g=o[p],v=g.instance,T=g.currentTarget;if(g=g.listener,v!==u&&a.isPropagationStopped())break e;Dc(a,g,T),u=v}else for(p=0;p<o.length;p++){if(g=o[p],v=g.instance,T=g.currentTarget,g=g.listener,v!==u&&a.isPropagationStopped())break e;Dc(a,g,T),u=v}}}if(Yo)throw e=Qs,Yo=!1,Qs=null,e}function Me(e,t){var r=t[jl];r===void 0&&(r=t[jl]=new Set);var o=e+"__bubble";r.has(o)||(bc(t,e,2,!1),r.add(o))}function xl(e,t,r){var o=0;t&&(o|=4),bc(r,e,o,t)}var li="_reactListening"+Math.random().toString(36).slice(2);function oo(e){if(!e[li]){e[li]=!0,l.forEach(function(r){r!=="selectionchange"&&(rg.has(r)||xl(r,!1,e),xl(r,!0,e))});var t=e.nodeType===9?e:e.ownerDocument;t===null||t[li]||(t[li]=!0,xl("selectionchange",!1,t))}}function bc(e,t,r,o){switch(lc(t)){case 1:var a=ym;break;case 4:a=xm;break;default:a=tl}r=a.bind(null,t,r,e),a=void 0,!qs||t!=="touchstart"&&t!=="touchmove"&&t!=="wheel"||(a=!0),o?a!==void 0?e.addEventListener(t,r,{capture:!0,passive:a}):e.addEventListener(t,r,!0):a!==void 0?e.addEventListener(t,r,{passive:a}):e.addEventListener(t,r,!1)}function vl(e,t,r,o,a){var u=o;if(!(t&1)&&!(t&2)&&o!==null)e:for(;;){if(o===null)return;var p=o.tag;if(p===3||p===4){var g=o.stateNode.containerInfo;if(g===a||g.nodeType===8&&g.parentNode===a)break;if(p===4)for(p=o.return;p!==null;){var v=p.tag;if((v===3||v===4)&&(v=p.stateNode.containerInfo,v===a||v.nodeType===8&&v.parentNode===a))return;p=p.return}for(;g!==null;){if(p=Mn(g),p===null)return;if(v=p.tag,v===5||v===6){o=u=p;continue e}g=g.parentNode}}o=o.return}Hu(function(){var T=u,Y=Ys(r),q=[];e:{var U=Oc.get(e);if(U!==void 0){var te=ol,re=e;switch(e){case"keypress":if(ti(r)===0)break e;case"keydown":case"keyup":te=$m;break;case"focusin":re="focus",te=ll;break;case"focusout":re="blur",te=ll;break;case"beforeblur":case"afterblur":te=ll;break;case"click":if(r.button===2)break e;case"auxclick":case"dblclick":case"mousedown":case"mousemove":case"mouseup":case"mouseout":case"mouseover":case"contextmenu":te=cc;break;case"drag":case"dragend":case"dragenter":case"dragexit":case"dragleave":case"dragover":case"dragstart":case"drop":te=Sm;break;case"touchcancel":case"touchend":case"touchmove":case"touchstart":te=Dm;break;case _c:case Tc:case Nc:te=Em;break;case $c:te=bm;break;case"scroll":te=vm;break;case"wheel":te=Bm;break;case"copy":case"cut":case"paste":te=Am;break;case"gotpointercapture":case"lostpointercapture":case"pointercancel":case"pointerdown":case"pointermove":case"pointerout":case"pointerover":case"pointerup":te=fc}var oe=(t&4)!==0,be=!oe&&e==="scroll",R=oe?U!==null?U+"Capture":null:U;oe=[];for(var C=T,M;C!==null;){M=C;var K=M.stateNode;if(M.tag===5&&K!==null&&(M=K,R!==null&&(K=Br(C,R),K!=null&&oe.push(io(C,K,M)))),be)break;C=C.return}0<oe.length&&(U=new te(U,re,null,r,Y),q.push({event:U,listeners:oe}))}}if(!(t&7)){e:{if(U=e==="mouseover"||e==="pointerover",te=e==="mouseout"||e==="pointerout",U&&r!==Hs&&(re=r.relatedTarget||r.fromElement)&&(Mn(re)||re[Qt]))break e;if((te||U)&&(U=Y.window===Y?Y:(U=Y.ownerDocument)?U.defaultView||U.parentWindow:window,te?(re=r.relatedTarget||r.toElement,te=T,re=re?Mn(re):null,re!==null&&(be=Pn(re),re!==be||re.tag!==5&&re.tag!==6)&&(re=null)):(te=null,re=T),te!==re)){if(oe=cc,K="onMouseLeave",R="onMouseEnter",C="mouse",(e==="pointerout"||e==="pointerover")&&(oe=fc,K="onPointerLeave",R="onPointerEnter",C="pointer"),be=te==null?U:lr(te),M=re==null?U:lr(re),U=new oe(K,C+"leave",te,r,Y),U.target=be,U.relatedTarget=M,K=null,Mn(Y)===T&&(oe=new oe(R,C+"enter",re,r,Y),oe.target=M,oe.relatedTarget=be,K=oe),be=K,te&&re)t:{for(oe=te,R=re,C=0,M=oe;M;M=ir(M))C++;for(M=0,K=R;K;K=ir(K))M++;for(;0<C-M;)oe=ir(oe),C--;for(;0<M-C;)R=ir(R),M--;for(;C--;){if(oe===R||R!==null&&oe===R.alternate)break t;oe=ir(oe),R=ir(R)}oe=null}else oe=null;te!==null&&zc(q,U,te,oe,!1),re!==null&&be!==null&&zc(q,be,re,oe,!0)}}e:{if(U=T?lr(T):window,te=U.nodeName&&U.nodeName.toLowerCase(),te==="select"||te==="input"&&U.type==="file")var se=qm;else if(xc(U))if(wc)se=Xm;else{se=Gm;var ue=Qm}else(te=U.nodeName)&&te.toLowerCase()==="input"&&(U.type==="checkbox"||U.type==="radio")&&(se=Km);if(se&&(se=se(e,T))){vc(q,se,r,Y);break e}ue&&ue(e,U,T),e==="focusout"&&(ue=U._wrapperState)&&ue.controlled&&U.type==="number"&&bs(U,"number",U.value)}switch(ue=T?lr(T):window,e){case"focusin":(xc(ue)||ue.contentEditable==="true")&&(rr=ue,pl=T,no=null);break;case"focusout":no=pl=rr=null;break;case"mousedown":hl=!0;break;case"contextmenu":case"mouseup":case"dragend":hl=!1,Pc(q,r,Y);break;case"selectionchange":if(eg)break;case"keydown":case"keyup":Pc(q,r,Y)}var ce;if(ul)e:{switch(e){case"compositionstart":var fe="onCompositionStart";break e;case"compositionend":fe="onCompositionEnd";break e;case"compositionupdate":fe="onCompositionUpdate";break e}fe=void 0}else nr?gc(e,r)&&(fe="onCompositionEnd"):e==="keydown"&&r.keyCode===229&&(fe="onCompositionStart");fe&&(pc&&r.locale!=="ko"&&(nr||fe!=="onCompositionStart"?fe==="onCompositionEnd"&&nr&&(ce=ac()):(un=Y,rl="value"in un?un.value:un.textContent,nr=!0)),ue=ai(T,fe),0<ue.length&&(fe=new dc(fe,e,null,r,Y),q.push({event:fe,listeners:ue}),ce?fe.data=ce:(ce=yc(r),ce!==null&&(fe.data=ce)))),(ce=Um?Hm(e,r):Ym(e,r))&&(T=ai(T,"onBeforeInput"),0<T.length&&(Y=new dc("onBeforeInput","beforeinput",null,r,Y),q.push({event:Y,listeners:T}),Y.data=ce))}Ic(q,t)})}function io(e,t,r){return{instance:e,listener:t,currentTarget:r}}function ai(e,t){for(var r=t+"Capture",o=[];e!==null;){var a=e,u=a.stateNode;a.tag===5&&u!==null&&(a=u,u=Br(e,r),u!=null&&o.unshift(io(e,u,a)),u=Br(e,t),u!=null&&o.push(io(e,u,a))),e=e.return}return o}function ir(e){if(e===null)return null;do e=e.return;while(e&&e.tag!==5);return e||null}function zc(e,t,r,o,a){for(var u=t._reactName,p=[];r!==null&&r!==o;){var g=r,v=g.alternate,T=g.stateNode;if(v!==null&&v===o)break;g.tag===5&&T!==null&&(g=T,a?(v=Br(r,u),v!=null&&p.unshift(io(r,v,g))):a||(v=Br(r,u),v!=null&&p.push(io(r,v,g)))),r=r.return}p.length!==0&&e.push({event:t,listeners:p})}var og=/\r\n?/g,ig=/\u0000|\uFFFD/g;function Bc(e){return(typeof e=="string"?e:""+e).replace(og,`
`).replace(ig,"")}function ui(e,t,r){if(t=Bc(t),Bc(e)!==t&&r)throw Error(s(425))}function ci(){}var wl=null,Sl=null;function kl(e,t){return e==="textarea"||e==="noscript"||typeof t.children=="string"||typeof t.children=="number"||typeof t.dangerouslySetInnerHTML=="object"&&t.dangerouslySetInnerHTML!==null&&t.dangerouslySetInnerHTML.__html!=null}var Cl=typeof setTimeout=="function"?setTimeout:void 0,sg=typeof clearTimeout=="function"?clearTimeout:void 0,Fc=typeof Promise=="function"?Promise:void 0,lg=typeof queueMicrotask=="function"?queueMicrotask:typeof Fc<"u"?function(e){return Fc.resolve(null).then(e).catch(ag)}:Cl;function ag(e){setTimeout(function(){throw e})}function El(e,t){var r=t,o=0;do{var a=r.nextSibling;if(e.removeChild(r),a&&a.nodeType===8)if(r=a.data,r==="/$"){if(o===0){e.removeChild(a),Gr(t);return}o--}else r!=="$"&&r!=="$?"&&r!=="$!"||o++;r=a}while(r);Gr(t)}function dn(e){for(;e!=null;e=e.nextSibling){var t=e.nodeType;if(t===1||t===3)break;if(t===8){if(t=e.data,t==="$"||t==="$!"||t==="$?")break;if(t==="/$")return null}}return e}function Uc(e){e=e.previousSibling;for(var t=0;e;){if(e.nodeType===8){var r=e.data;if(r==="$"||r==="$!"||r==="$?"){if(t===0)return e;t--}else r==="/$"&&t++}e=e.previousSibling}return null}var sr=Math.random().toString(36).slice(2),Ft="__reactFiber$"+sr,so="__reactProps$"+sr,Qt="__reactContainer$"+sr,jl="__reactEvents$"+sr,ug="__reactListeners$"+sr,cg="__reactHandles$"+sr;function Mn(e){var t=e[Ft];if(t)return t;for(var r=e.parentNode;r;){if(t=r[Qt]||r[Ft]){if(r=t.alternate,t.child!==null||r!==null&&r.child!==null)for(e=Uc(e);e!==null;){if(r=e[Ft])return r;e=Uc(e)}return t}e=r,r=e.parentNode}return null}function lo(e){return e=e[Ft]||e[Qt],!e||e.tag!==5&&e.tag!==6&&e.tag!==13&&e.tag!==3?null:e}function lr(e){if(e.tag===5||e.tag===6)return e.stateNode;throw Error(s(33))}function di(e){return e[so]||null}var Al=[],ar=-1;function fn(e){return{current:e}}function _e(e){0>ar||(e.current=Al[ar],Al[ar]=null,ar--)}function Pe(e,t){ar++,Al[ar]=e.current,e.current=t}var pn={},tt=fn(pn),ut=fn(!1),_n=pn;function ur(e,t){var r=e.type.contextTypes;if(!r)return pn;var o=e.stateNode;if(o&&o.__reactInternalMemoizedUnmaskedChildContext===t)return o.__reactInternalMemoizedMaskedChildContext;var a={},u;for(u in r)a[u]=t[u];return o&&(e=e.stateNode,e.__reactInternalMemoizedUnmaskedChildContext=t,e.__reactInternalMemoizedMaskedChildContext=a),a}function ct(e){return e=e.childContextTypes,e!=null}function fi(){_e(ut),_e(tt)}function Hc(e,t,r){if(tt.current!==pn)throw Error(s(168));Pe(tt,t),Pe(ut,r)}function Yc(e,t,r){var o=e.stateNode;if(t=t.childContextTypes,typeof o.getChildContext!="function")return r;o=o.getChildContext();for(var a in o)if(!(a in t))throw Error(s(108,we(e)||"Unknown",a));return B({},r,o)}function pi(e){return e=(e=e.stateNode)&&e.__reactInternalMemoizedMergedChildContext||pn,_n=tt.current,Pe(tt,e),Pe(ut,ut.current),!0}function Vc(e,t,r){var o=e.stateNode;if(!o)throw Error(s(169));r?(e=Yc(e,t,_n),o.__reactInternalMemoizedMergedChildContext=e,_e(ut),_e(tt),Pe(tt,e)):_e(ut),Pe(ut,r)}var Gt=null,hi=!1,Rl=!1;function Wc(e){Gt===null?Gt=[e]:Gt.push(e)}function dg(e){hi=!0,Wc(e)}function hn(){if(!Rl&&Gt!==null){Rl=!0;var e=0,t=Re;try{var r=Gt;for(Re=1;e<r.length;e++){var o=r[e];do o=o(!0);while(o!==null)}Gt=null,hi=!1}catch(a){throw Gt!==null&&(Gt=Gt.slice(e+1)),Qu(Gs,hn),a}finally{Re=t,Rl=!1}}return null}var cr=[],dr=0,mi=null,gi=0,Et=[],jt=0,Tn=null,Kt=1,Xt="";function Nn(e,t){cr[dr++]=gi,cr[dr++]=mi,mi=e,gi=t}function qc(e,t,r){Et[jt++]=Kt,Et[jt++]=Xt,Et[jt++]=Tn,Tn=e;var o=Kt;e=Xt;var a=32-Tt(o)-1;o&=~(1<<a),r+=1;var u=32-Tt(t)+a;if(30<u){var p=a-a%5;u=(o&(1<<p)-1).toString(32),o>>=p,a-=p,Kt=1<<32-Tt(t)+a|r<<a|o,Xt=u+e}else Kt=1<<u|r<<a|o,Xt=e}function Pl(e){e.return!==null&&(Nn(e,1),qc(e,1,0))}function Ml(e){for(;e===mi;)mi=cr[--dr],cr[dr]=null,gi=cr[--dr],cr[dr]=null;for(;e===Tn;)Tn=Et[--jt],Et[jt]=null,Xt=Et[--jt],Et[jt]=null,Kt=Et[--jt],Et[jt]=null}var yt=null,xt=null,Ne=!1,$t=null;function Qc(e,t){var r=Mt(5,null,null,0);r.elementType="DELETED",r.stateNode=t,r.return=e,t=e.deletions,t===null?(e.deletions=[r],e.flags|=16):t.push(r)}function Gc(e,t){switch(e.tag){case 5:var r=e.type;return t=t.nodeType!==1||r.toLowerCase()!==t.nodeName.toLowerCase()?null:t,t!==null?(e.stateNode=t,yt=e,xt=dn(t.firstChild),!0):!1;case 6:return t=e.pendingProps===""||t.nodeType!==3?null:t,t!==null?(e.stateNode=t,yt=e,xt=null,!0):!1;case 13:return t=t.nodeType!==8?null:t,t!==null?(r=Tn!==null?{id:Kt,overflow:Xt}:null,e.memoizedState={dehydrated:t,treeContext:r,retryLane:1073741824},r=Mt(18,null,null,0),r.stateNode=t,r.return=e,e.child=r,yt=e,xt=null,!0):!1;default:return!1}}function _l(e){return(e.mode&1)!==0&&(e.flags&128)===0}function Tl(e){if(Ne){var t=xt;if(t){var r=t;if(!Gc(e,t)){if(_l(e))throw Error(s(418));t=dn(r.nextSibling);var o=yt;t&&Gc(e,t)?Qc(o,r):(e.flags=e.flags&-4097|2,Ne=!1,yt=e)}}else{if(_l(e))throw Error(s(418));e.flags=e.flags&-4097|2,Ne=!1,yt=e}}}function Kc(e){for(e=e.return;e!==null&&e.tag!==5&&e.tag!==3&&e.tag!==13;)e=e.return;yt=e}function yi(e){if(e!==yt)return!1;if(!Ne)return Kc(e),Ne=!0,!1;var t;if((t=e.tag!==3)&&!(t=e.tag!==5)&&(t=e.type,t=t!=="head"&&t!=="body"&&!kl(e.type,e.memoizedProps)),t&&(t=xt)){if(_l(e))throw Xc(),Error(s(418));for(;t;)Qc(e,t),t=dn(t.nextSibling)}if(Kc(e),e.tag===13){if(e=e.memoizedState,e=e!==null?e.dehydrated:null,!e)throw Error(s(317));e:{for(e=e.nextSibling,t=0;e;){if(e.nodeType===8){var r=e.data;if(r==="/$"){if(t===0){xt=dn(e.nextSibling);break e}t--}else r!=="$"&&r!=="$!"&&r!=="$?"||t++}e=e.nextSibling}xt=null}}else xt=yt?dn(e.stateNode.nextSibling):null;return!0}function Xc(){for(var e=xt;e;)e=dn(e.nextSibling)}function fr(){xt=yt=null,Ne=!1}function Nl(e){$t===null?$t=[e]:$t.push(e)}var fg=X.ReactCurrentBatchConfig;function ao(e,t,r){if(e=r.ref,e!==null&&typeof e!="function"&&typeof e!="object"){if(r._owner){if(r=r._owner,r){if(r.tag!==1)throw Error(s(309));var o=r.stateNode}if(!o)throw Error(s(147,e));var a=o,u=""+e;return t!==null&&t.ref!==null&&typeof t.ref=="function"&&t.ref._stringRef===u?t.ref:(t=function(p){var g=a.refs;p===null?delete g[u]:g[u]=p},t._stringRef=u,t)}if(typeof e!="string")throw Error(s(284));if(!r._owner)throw Error(s(290,e))}return e}function xi(e,t){throw e=Object.prototype.toString.call(t),Error(s(31,e==="[object Object]"?"object with keys {"+Object.keys(t).join(", ")+"}":e))}function Jc(e){var t=e._init;return t(e._payload)}function Zc(e){function t(R,C){if(e){var M=R.deletions;M===null?(R.deletions=[C],R.flags|=16):M.push(C)}}function r(R,C){if(!e)return null;for(;C!==null;)t(R,C),C=C.sibling;return null}function o(R,C){for(R=new Map;C!==null;)C.key!==null?R.set(C.key,C):R.set(C.index,C),C=C.sibling;return R}function a(R,C){return R=kn(R,C),R.index=0,R.sibling=null,R}function u(R,C,M){return R.index=M,e?(M=R.alternate,M!==null?(M=M.index,M<C?(R.flags|=2,C):M):(R.flags|=2,C)):(R.flags|=1048576,C)}function p(R){return e&&R.alternate===null&&(R.flags|=2),R}function g(R,C,M,K){return C===null||C.tag!==6?(C=Ca(M,R.mode,K),C.return=R,C):(C=a(C,M),C.return=R,C)}function v(R,C,M,K){var se=M.type;return se===Q?Y(R,C,M.props.children,K,M.key):C!==null&&(C.elementType===se||typeof se=="object"&&se!==null&&se.$$typeof===b&&Jc(se)===C.type)?(K=a(C,M.props),K.ref=ao(R,C,M),K.return=R,K):(K=Ui(M.type,M.key,M.props,null,R.mode,K),K.ref=ao(R,C,M),K.return=R,K)}function T(R,C,M,K){return C===null||C.tag!==4||C.stateNode.containerInfo!==M.containerInfo||C.stateNode.implementation!==M.implementation?(C=Ea(M,R.mode,K),C.return=R,C):(C=a(C,M.children||[]),C.return=R,C)}function Y(R,C,M,K,se){return C===null||C.tag!==7?(C=Bn(M,R.mode,K,se),C.return=R,C):(C=a(C,M),C.return=R,C)}function q(R,C,M){if(typeof C=="string"&&C!==""||typeof C=="number")return C=Ca(""+C,R.mode,M),C.return=R,C;if(typeof C=="object"&&C!==null){switch(C.$$typeof){case D:return M=Ui(C.type,C.key,C.props,null,R.mode,M),M.ref=ao(R,null,C),M.return=R,M;case N:return C=Ea(C,R.mode,M),C.return=R,C;case b:var K=C._init;return q(R,K(C._payload),M)}if(Ir(C)||F(C))return C=Bn(C,R.mode,M,null),C.return=R,C;xi(R,C)}return null}function U(R,C,M,K){var se=C!==null?C.key:null;if(typeof M=="string"&&M!==""||typeof M=="number")return se!==null?null:g(R,C,""+M,K);if(typeof M=="object"&&M!==null){switch(M.$$typeof){case D:return M.key===se?v(R,C,M,K):null;case N:return M.key===se?T(R,C,M,K):null;case b:return se=M._init,U(R,C,se(M._payload),K)}if(Ir(M)||F(M))return se!==null?null:Y(R,C,M,K,null);xi(R,M)}return null}function te(R,C,M,K,se){if(typeof K=="string"&&K!==""||typeof K=="number")return R=R.get(M)||null,g(C,R,""+K,se);if(typeof K=="object"&&K!==null){switch(K.$$typeof){case D:return R=R.get(K.key===null?M:K.key)||null,v(C,R,K,se);case N:return R=R.get(K.key===null?M:K.key)||null,T(C,R,K,se);case b:var ue=K._init;return te(R,C,M,ue(K._payload),se)}if(Ir(K)||F(K))return R=R.get(M)||null,Y(C,R,K,se,null);xi(C,K)}return null}function re(R,C,M,K){for(var se=null,ue=null,ce=C,fe=C=0,Ge=null;ce!==null&&fe<M.length;fe++){ce.index>fe?(Ge=ce,ce=null):Ge=ce.sibling;var je=U(R,ce,M[fe],K);if(je===null){ce===null&&(ce=Ge);break}e&&ce&&je.alternate===null&&t(R,ce),C=u(je,C,fe),ue===null?se=je:ue.sibling=je,ue=je,ce=Ge}if(fe===M.length)return r(R,ce),Ne&&Nn(R,fe),se;if(ce===null){for(;fe<M.length;fe++)ce=q(R,M[fe],K),ce!==null&&(C=u(ce,C,fe),ue===null?se=ce:ue.sibling=ce,ue=ce);return Ne&&Nn(R,fe),se}for(ce=o(R,ce);fe<M.length;fe++)Ge=te(ce,R,fe,M[fe],K),Ge!==null&&(e&&Ge.alternate!==null&&ce.delete(Ge.key===null?fe:Ge.key),C=u(Ge,C,fe),ue===null?se=Ge:ue.sibling=Ge,ue=Ge);return e&&ce.forEach(function(Cn){return t(R,Cn)}),Ne&&Nn(R,fe),se}function oe(R,C,M,K){var se=F(M);if(typeof se!="function")throw Error(s(150));if(M=se.call(M),M==null)throw Error(s(151));for(var ue=se=null,ce=C,fe=C=0,Ge=null,je=M.next();ce!==null&&!je.done;fe++,je=M.next()){ce.index>fe?(Ge=ce,ce=null):Ge=ce.sibling;var Cn=U(R,ce,je.value,K);if(Cn===null){ce===null&&(ce=Ge);break}e&&ce&&Cn.alternate===null&&t(R,ce),C=u(Cn,C,fe),ue===null?se=Cn:ue.sibling=Cn,ue=Cn,ce=Ge}if(je.done)return r(R,ce),Ne&&Nn(R,fe),se;if(ce===null){for(;!je.done;fe++,je=M.next())je=q(R,je.value,K),je!==null&&(C=u(je,C,fe),ue===null?se=je:ue.sibling=je,ue=je);return Ne&&Nn(R,fe),se}for(ce=o(R,ce);!je.done;fe++,je=M.next())je=te(ce,R,fe,je.value,K),je!==null&&(e&&je.alternate!==null&&ce.delete(je.key===null?fe:je.key),C=u(je,C,fe),ue===null?se=je:ue.sibling=je,ue=je);return e&&ce.forEach(function(Yg){return t(R,Yg)}),Ne&&Nn(R,fe),se}function be(R,C,M,K){if(typeof M=="object"&&M!==null&&M.type===Q&&M.key===null&&(M=M.props.children),typeof M=="object"&&M!==null){switch(M.$$typeof){case D:e:{for(var se=M.key,ue=C;ue!==null;){if(ue.key===se){if(se=M.type,se===Q){if(ue.tag===7){r(R,ue.sibling),C=a(ue,M.props.children),C.return=R,R=C;break e}}else if(ue.elementType===se||typeof se=="object"&&se!==null&&se.$$typeof===b&&Jc(se)===ue.type){r(R,ue.sibling),C=a(ue,M.props),C.ref=ao(R,ue,M),C.return=R,R=C;break e}r(R,ue);break}else t(R,ue);ue=ue.sibling}M.type===Q?(C=Bn(M.props.children,R.mode,K,M.key),C.return=R,R=C):(K=Ui(M.type,M.key,M.props,null,R.mode,K),K.ref=ao(R,C,M),K.return=R,R=K)}return p(R);case N:e:{for(ue=M.key;C!==null;){if(C.key===ue)if(C.tag===4&&C.stateNode.containerInfo===M.containerInfo&&C.stateNode.implementation===M.implementation){r(R,C.sibling),C=a(C,M.children||[]),C.return=R,R=C;break e}else{r(R,C);break}else t(R,C);C=C.sibling}C=Ea(M,R.mode,K),C.return=R,R=C}return p(R);case b:return ue=M._init,be(R,C,ue(M._payload),K)}if(Ir(M))return re(R,C,M,K);if(F(M))return oe(R,C,M,K);xi(R,M)}return typeof M=="string"&&M!==""||typeof M=="number"?(M=""+M,C!==null&&C.tag===6?(r(R,C.sibling),C=a(C,M),C.return=R,R=C):(r(R,C),C=Ca(M,R.mode,K),C.return=R,R=C),p(R)):r(R,C)}return be}var pr=Zc(!0),ed=Zc(!1),vi=fn(null),wi=null,hr=null,$l=null;function Ol(){$l=hr=wi=null}function Ll(e){var t=vi.current;_e(vi),e._currentValue=t}function Dl(e,t,r){for(;e!==null;){var o=e.alternate;if((e.childLanes&t)!==t?(e.childLanes|=t,o!==null&&(o.childLanes|=t)):o!==null&&(o.childLanes&t)!==t&&(o.childLanes|=t),e===r)break;e=e.return}}function mr(e,t){wi=e,$l=hr=null,e=e.dependencies,e!==null&&e.firstContext!==null&&(e.lanes&t&&(dt=!0),e.firstContext=null)}function At(e){var t=e._currentValue;if($l!==e)if(e={context:e,memoizedValue:t,next:null},hr===null){if(wi===null)throw Error(s(308));hr=e,wi.dependencies={lanes:0,firstContext:e}}else hr=hr.next=e;return t}var $n=null;function Il(e){$n===null?$n=[e]:$n.push(e)}function td(e,t,r,o){var a=t.interleaved;return a===null?(r.next=r,Il(t)):(r.next=a.next,a.next=r),t.interleaved=r,Jt(e,o)}function Jt(e,t){e.lanes|=t;var r=e.alternate;for(r!==null&&(r.lanes|=t),r=e,e=e.return;e!==null;)e.childLanes|=t,r=e.alternate,r!==null&&(r.childLanes|=t),r=e,e=e.return;return r.tag===3?r.stateNode:null}var mn=!1;function bl(e){e.updateQueue={baseState:e.memoizedState,firstBaseUpdate:null,lastBaseUpdate:null,shared:{pending:null,interleaved:null,lanes:0},effects:null}}function nd(e,t){e=e.updateQueue,t.updateQueue===e&&(t.updateQueue={baseState:e.baseState,firstBaseUpdate:e.firstBaseUpdate,lastBaseUpdate:e.lastBaseUpdate,shared:e.shared,effects:e.effects})}function Zt(e,t){return{eventTime:e,lane:t,tag:0,payload:null,callback:null,next:null}}function gn(e,t,r){var o=e.updateQueue;if(o===null)return null;if(o=o.shared,Ce&2){var a=o.pending;return a===null?t.next=t:(t.next=a.next,a.next=t),o.pending=t,Jt(e,r)}return a=o.interleaved,a===null?(t.next=t,Il(o)):(t.next=a.next,a.next=t),o.interleaved=t,Jt(e,r)}function Si(e,t,r){if(t=t.updateQueue,t!==null&&(t=t.shared,(r&4194240)!==0)){var o=t.lanes;o&=e.pendingLanes,r|=o,t.lanes=r,Js(e,r)}}function rd(e,t){var r=e.updateQueue,o=e.alternate;if(o!==null&&(o=o.updateQueue,r===o)){var a=null,u=null;if(r=r.firstBaseUpdate,r!==null){do{var p={eventTime:r.eventTime,lane:r.lane,tag:r.tag,payload:r.payload,callback:r.callback,next:null};u===null?a=u=p:u=u.next=p,r=r.next}while(r!==null);u===null?a=u=t:u=u.next=t}else a=u=t;r={baseState:o.baseState,firstBaseUpdate:a,lastBaseUpdate:u,shared:o.shared,effects:o.effects},e.updateQueue=r;return}e=r.lastBaseUpdate,e===null?r.firstBaseUpdate=t:e.next=t,r.lastBaseUpdate=t}function ki(e,t,r,o){var a=e.updateQueue;mn=!1;var u=a.firstBaseUpdate,p=a.lastBaseUpdate,g=a.shared.pending;if(g!==null){a.shared.pending=null;var v=g,T=v.next;v.next=null,p===null?u=T:p.next=T,p=v;var Y=e.alternate;Y!==null&&(Y=Y.updateQueue,g=Y.lastBaseUpdate,g!==p&&(g===null?Y.firstBaseUpdate=T:g.next=T,Y.lastBaseUpdate=v))}if(u!==null){var q=a.baseState;p=0,Y=T=v=null,g=u;do{var U=g.lane,te=g.eventTime;if((o&U)===U){Y!==null&&(Y=Y.next={eventTime:te,lane:0,tag:g.tag,payload:g.payload,callback:g.callback,next:null});e:{var re=e,oe=g;switch(U=t,te=r,oe.tag){case 1:if(re=oe.payload,typeof re=="function"){q=re.call(te,q,U);break e}q=re;break e;case 3:re.flags=re.flags&-65537|128;case 0:if(re=oe.payload,U=typeof re=="function"?re.call(te,q,U):re,U==null)break e;q=B({},q,U);break e;case 2:mn=!0}}g.callback!==null&&g.lane!==0&&(e.flags|=64,U=a.effects,U===null?a.effects=[g]:U.push(g))}else te={eventTime:te,lane:U,tag:g.tag,payload:g.payload,callback:g.callback,next:null},Y===null?(T=Y=te,v=q):Y=Y.next=te,p|=U;if(g=g.next,g===null){if(g=a.shared.pending,g===null)break;U=g,g=U.next,U.next=null,a.lastBaseUpdate=U,a.shared.pending=null}}while(!0);if(Y===null&&(v=q),a.baseState=v,a.firstBaseUpdate=T,a.lastBaseUpdate=Y,t=a.shared.interleaved,t!==null){a=t;do p|=a.lane,a=a.next;while(a!==t)}else u===null&&(a.shared.lanes=0);Dn|=p,e.lanes=p,e.memoizedState=q}}function od(e,t,r){if(e=t.effects,t.effects=null,e!==null)for(t=0;t<e.length;t++){var o=e[t],a=o.callback;if(a!==null){if(o.callback=null,o=r,typeof a!="function")throw Error(s(191,a));a.call(o)}}}var uo={},Ut=fn(uo),co=fn(uo),fo=fn(uo);function On(e){if(e===uo)throw Error(s(174));return e}function zl(e,t){switch(Pe(fo,t),Pe(co,e),Pe(Ut,uo),e=t.nodeType,e){case 9:case 11:t=(t=t.documentElement)?t.namespaceURI:Bs(null,"");break;default:e=e===8?t.parentNode:t,t=e.namespaceURI||null,e=e.tagName,t=Bs(t,e)}_e(Ut),Pe(Ut,t)}function gr(){_e(Ut),_e(co),_e(fo)}function id(e){On(fo.current);var t=On(Ut.current),r=Bs(t,e.type);t!==r&&(Pe(co,e),Pe(Ut,r))}function Bl(e){co.current===e&&(_e(Ut),_e(co))}var $e=fn(0);function Ci(e){for(var t=e;t!==null;){if(t.tag===13){var r=t.memoizedState;if(r!==null&&(r=r.dehydrated,r===null||r.data==="$?"||r.data==="$!"))return t}else if(t.tag===19&&t.memoizedProps.revealOrder!==void 0){if(t.flags&128)return t}else if(t.child!==null){t.child.return=t,t=t.child;continue}if(t===e)break;for(;t.sibling===null;){if(t.return===null||t.return===e)return null;t=t.return}t.sibling.return=t.return,t=t.sibling}return null}var Fl=[];function Ul(){for(var e=0;e<Fl.length;e++)Fl[e]._workInProgressVersionPrimary=null;Fl.length=0}var Ei=X.ReactCurrentDispatcher,Hl=X.ReactCurrentBatchConfig,Ln=0,Oe=null,Ye=null,qe=null,ji=!1,po=!1,ho=0,pg=0;function nt(){throw Error(s(321))}function Yl(e,t){if(t===null)return!1;for(var r=0;r<t.length&&r<e.length;r++)if(!Nt(e[r],t[r]))return!1;return!0}function Vl(e,t,r,o,a,u){if(Ln=u,Oe=t,t.memoizedState=null,t.updateQueue=null,t.lanes=0,Ei.current=e===null||e.memoizedState===null?yg:xg,e=r(o,a),po){u=0;do{if(po=!1,ho=0,25<=u)throw Error(s(301));u+=1,qe=Ye=null,t.updateQueue=null,Ei.current=vg,e=r(o,a)}while(po)}if(Ei.current=Pi,t=Ye!==null&&Ye.next!==null,Ln=0,qe=Ye=Oe=null,ji=!1,t)throw Error(s(300));return e}function Wl(){var e=ho!==0;return ho=0,e}function Ht(){var e={memoizedState:null,baseState:null,baseQueue:null,queue:null,next:null};return qe===null?Oe.memoizedState=qe=e:qe=qe.next=e,qe}function Rt(){if(Ye===null){var e=Oe.alternate;e=e!==null?e.memoizedState:null}else e=Ye.next;var t=qe===null?Oe.memoizedState:qe.next;if(t!==null)qe=t,Ye=e;else{if(e===null)throw Error(s(310));Ye=e,e={memoizedState:Ye.memoizedState,baseState:Ye.baseState,baseQueue:Ye.baseQueue,queue:Ye.queue,next:null},qe===null?Oe.memoizedState=qe=e:qe=qe.next=e}return qe}function mo(e,t){return typeof t=="function"?t(e):t}function ql(e){var t=Rt(),r=t.queue;if(r===null)throw Error(s(311));r.lastRenderedReducer=e;var o=Ye,a=o.baseQueue,u=r.pending;if(u!==null){if(a!==null){var p=a.next;a.next=u.next,u.next=p}o.baseQueue=a=u,r.pending=null}if(a!==null){u=a.next,o=o.baseState;var g=p=null,v=null,T=u;do{var Y=T.lane;if((Ln&Y)===Y)v!==null&&(v=v.next={lane:0,action:T.action,hasEagerState:T.hasEagerState,eagerState:T.eagerState,next:null}),o=T.hasEagerState?T.eagerState:e(o,T.action);else{var q={lane:Y,action:T.action,hasEagerState:T.hasEagerState,eagerState:T.eagerState,next:null};v===null?(g=v=q,p=o):v=v.next=q,Oe.lanes|=Y,Dn|=Y}T=T.next}while(T!==null&&T!==u);v===null?p=o:v.next=g,Nt(o,t.memoizedState)||(dt=!0),t.memoizedState=o,t.baseState=p,t.baseQueue=v,r.lastRenderedState=o}if(e=r.interleaved,e!==null){a=e;do u=a.lane,Oe.lanes|=u,Dn|=u,a=a.next;while(a!==e)}else a===null&&(r.lanes=0);return[t.memoizedState,r.dispatch]}function Ql(e){var t=Rt(),r=t.queue;if(r===null)throw Error(s(311));r.lastRenderedReducer=e;var o=r.dispatch,a=r.pending,u=t.memoizedState;if(a!==null){r.pending=null;var p=a=a.next;do u=e(u,p.action),p=p.next;while(p!==a);Nt(u,t.memoizedState)||(dt=!0),t.memoizedState=u,t.baseQueue===null&&(t.baseState=u),r.lastRenderedState=u}return[u,o]}function sd(){}function ld(e,t){var r=Oe,o=Rt(),a=t(),u=!Nt(o.memoizedState,a);if(u&&(o.memoizedState=a,dt=!0),o=o.queue,Gl(cd.bind(null,r,o,e),[e]),o.getSnapshot!==t||u||qe!==null&&qe.memoizedState.tag&1){if(r.flags|=2048,go(9,ud.bind(null,r,o,a,t),void 0,null),Qe===null)throw Error(s(349));Ln&30||ad(r,t,a)}return a}function ad(e,t,r){e.flags|=16384,e={getSnapshot:t,value:r},t=Oe.updateQueue,t===null?(t={lastEffect:null,stores:null},Oe.updateQueue=t,t.stores=[e]):(r=t.stores,r===null?t.stores=[e]:r.push(e))}function ud(e,t,r,o){t.value=r,t.getSnapshot=o,dd(t)&&fd(e)}function cd(e,t,r){return r(function(){dd(t)&&fd(e)})}function dd(e){var t=e.getSnapshot;e=e.value;try{var r=t();return!Nt(e,r)}catch{return!0}}function fd(e){var t=Jt(e,1);t!==null&&It(t,e,1,-1)}function pd(e){var t=Ht();return typeof e=="function"&&(e=e()),t.memoizedState=t.baseState=e,e={pending:null,interleaved:null,lanes:0,dispatch:null,lastRenderedReducer:mo,lastRenderedState:e},t.queue=e,e=e.dispatch=gg.bind(null,Oe,e),[t.memoizedState,e]}function go(e,t,r,o){return e={tag:e,create:t,destroy:r,deps:o,next:null},t=Oe.updateQueue,t===null?(t={lastEffect:null,stores:null},Oe.updateQueue=t,t.lastEffect=e.next=e):(r=t.lastEffect,r===null?t.lastEffect=e.next=e:(o=r.next,r.next=e,e.next=o,t.lastEffect=e)),e}function hd(){return Rt().memoizedState}function Ai(e,t,r,o){var a=Ht();Oe.flags|=e,a.memoizedState=go(1|t,r,void 0,o===void 0?null:o)}function Ri(e,t,r,o){var a=Rt();o=o===void 0?null:o;var u=void 0;if(Ye!==null){var p=Ye.memoizedState;if(u=p.destroy,o!==null&&Yl(o,p.deps)){a.memoizedState=go(t,r,u,o);return}}Oe.flags|=e,a.memoizedState=go(1|t,r,u,o)}function md(e,t){return Ai(8390656,8,e,t)}function Gl(e,t){return Ri(2048,8,e,t)}function gd(e,t){return Ri(4,2,e,t)}function yd(e,t){return Ri(4,4,e,t)}function xd(e,t){if(typeof t=="function")return e=e(),t(e),function(){t(null)};if(t!=null)return e=e(),t.current=e,function(){t.current=null}}function vd(e,t,r){return r=r!=null?r.concat([e]):null,Ri(4,4,xd.bind(null,t,e),r)}function Kl(){}function wd(e,t){var r=Rt();t=t===void 0?null:t;var o=r.memoizedState;return o!==null&&t!==null&&Yl(t,o[1])?o[0]:(r.memoizedState=[e,t],e)}function Sd(e,t){var r=Rt();t=t===void 0?null:t;var o=r.memoizedState;return o!==null&&t!==null&&Yl(t,o[1])?o[0]:(e=e(),r.memoizedState=[e,t],e)}function kd(e,t,r){return Ln&21?(Nt(r,t)||(r=Ju(),Oe.lanes|=r,Dn|=r,e.baseState=!0),t):(e.baseState&&(e.baseState=!1,dt=!0),e.memoizedState=r)}function hg(e,t){var r=Re;Re=r!==0&&4>r?r:4,e(!0);var o=Hl.transition;Hl.transition={};try{e(!1),t()}finally{Re=r,Hl.transition=o}}function Cd(){return Rt().memoizedState}function mg(e,t,r){var o=wn(e);if(r={lane:o,action:r,hasEagerState:!1,eagerState:null,next:null},Ed(e))jd(t,r);else if(r=td(e,t,r,o),r!==null){var a=at();It(r,e,o,a),Ad(r,t,o)}}function gg(e,t,r){var o=wn(e),a={lane:o,action:r,hasEagerState:!1,eagerState:null,next:null};if(Ed(e))jd(t,a);else{var u=e.alternate;if(e.lanes===0&&(u===null||u.lanes===0)&&(u=t.lastRenderedReducer,u!==null))try{var p=t.lastRenderedState,g=u(p,r);if(a.hasEagerState=!0,a.eagerState=g,Nt(g,p)){var v=t.interleaved;v===null?(a.next=a,Il(t)):(a.next=v.next,v.next=a),t.interleaved=a;return}}catch{}finally{}r=td(e,t,a,o),r!==null&&(a=at(),It(r,e,o,a),Ad(r,t,o))}}function Ed(e){var t=e.alternate;return e===Oe||t!==null&&t===Oe}function jd(e,t){po=ji=!0;var r=e.pending;r===null?t.next=t:(t.next=r.next,r.next=t),e.pending=t}function Ad(e,t,r){if(r&4194240){var o=t.lanes;o&=e.pendingLanes,r|=o,t.lanes=r,Js(e,r)}}var Pi={readContext:At,useCallback:nt,useContext:nt,useEffect:nt,useImperativeHandle:nt,useInsertionEffect:nt,useLayoutEffect:nt,useMemo:nt,useReducer:nt,useRef:nt,useState:nt,useDebugValue:nt,useDeferredValue:nt,useTransition:nt,useMutableSource:nt,useSyncExternalStore:nt,useId:nt,unstable_isNewReconciler:!1},yg={readContext:At,useCallback:function(e,t){return Ht().memoizedState=[e,t===void 0?null:t],e},useContext:At,useEffect:md,useImperativeHandle:function(e,t,r){return r=r!=null?r.concat([e]):null,Ai(4194308,4,xd.bind(null,t,e),r)},useLayoutEffect:function(e,t){return Ai(4194308,4,e,t)},useInsertionEffect:function(e,t){return Ai(4,2,e,t)},useMemo:function(e,t){var r=Ht();return t=t===void 0?null:t,e=e(),r.memoizedState=[e,t],e},useReducer:function(e,t,r){var o=Ht();return t=r!==void 0?r(t):t,o.memoizedState=o.baseState=t,e={pending:null,interleaved:null,lanes:0,dispatch:null,lastRenderedReducer:e,lastRenderedState:t},o.queue=e,e=e.dispatch=mg.bind(null,Oe,e),[o.memoizedState,e]},useRef:function(e){var t=Ht();return e={current:e},t.memoizedState=e},useState:pd,useDebugValue:Kl,useDeferredValue:function(e){return Ht().memoizedState=e},useTransition:function(){var e=pd(!1),t=e[0];return e=hg.bind(null,e[1]),Ht().memoizedState=e,[t,e]},useMutableSource:function(){},useSyncExternalStore:function(e,t,r){var o=Oe,a=Ht();if(Ne){if(r===void 0)throw Error(s(407));r=r()}else{if(r=t(),Qe===null)throw Error(s(349));Ln&30||ad(o,t,r)}a.memoizedState=r;var u={value:r,getSnapshot:t};return a.queue=u,md(cd.bind(null,o,u,e),[e]),o.flags|=2048,go(9,ud.bind(null,o,u,r,t),void 0,null),r},useId:function(){var e=Ht(),t=Qe.identifierPrefix;if(Ne){var r=Xt,o=Kt;r=(o&~(1<<32-Tt(o)-1)).toString(32)+r,t=":"+t+"R"+r,r=ho++,0<r&&(t+="H"+r.toString(32)),t+=":"}else r=pg++,t=":"+t+"r"+r.toString(32)+":";return e.memoizedState=t},unstable_isNewReconciler:!1},xg={readContext:At,useCallback:wd,useContext:At,useEffect:Gl,useImperativeHandle:vd,useInsertionEffect:gd,useLayoutEffect:yd,useMemo:Sd,useReducer:ql,useRef:hd,useState:function(){return ql(mo)},useDebugValue:Kl,useDeferredValue:function(e){var t=Rt();return kd(t,Ye.memoizedState,e)},useTransition:function(){var e=ql(mo)[0],t=Rt().memoizedState;return[e,t]},useMutableSource:sd,useSyncExternalStore:ld,useId:Cd,unstable_isNewReconciler:!1},vg={readContext:At,useCallback:wd,useContext:At,useEffect:Gl,useImperativeHandle:vd,useInsertionEffect:gd,useLayoutEffect:yd,useMemo:Sd,useReducer:Ql,useRef:hd,useState:function(){return Ql(mo)},useDebugValue:Kl,useDeferredValue:function(e){var t=Rt();return Ye===null?t.memoizedState=e:kd(t,Ye.memoizedState,e)},useTransition:function(){var e=Ql(mo)[0],t=Rt().memoizedState;return[e,t]},useMutableSource:sd,useSyncExternalStore:ld,useId:Cd,unstable_isNewReconciler:!1};function Ot(e,t){if(e&&e.defaultProps){t=B({},t),e=e.defaultProps;for(var r in e)t[r]===void 0&&(t[r]=e[r]);return t}return t}function Xl(e,t,r,o){t=e.memoizedState,r=r(o,t),r=r==null?t:B({},t,r),e.memoizedState=r,e.lanes===0&&(e.updateQueue.baseState=r)}var Mi={isMounted:function(e){return(e=e._reactInternals)?Pn(e)===e:!1},enqueueSetState:function(e,t,r){e=e._reactInternals;var o=at(),a=wn(e),u=Zt(o,a);u.payload=t,r!=null&&(u.callback=r),t=gn(e,u,a),t!==null&&(It(t,e,a,o),Si(t,e,a))},enqueueReplaceState:function(e,t,r){e=e._reactInternals;var o=at(),a=wn(e),u=Zt(o,a);u.tag=1,u.payload=t,r!=null&&(u.callback=r),t=gn(e,u,a),t!==null&&(It(t,e,a,o),Si(t,e,a))},enqueueForceUpdate:function(e,t){e=e._reactInternals;var r=at(),o=wn(e),a=Zt(r,o);a.tag=2,t!=null&&(a.callback=t),t=gn(e,a,o),t!==null&&(It(t,e,o,r),Si(t,e,o))}};function Rd(e,t,r,o,a,u,p){return e=e.stateNode,typeof e.shouldComponentUpdate=="function"?e.shouldComponentUpdate(o,u,p):t.prototype&&t.prototype.isPureReactComponent?!to(r,o)||!to(a,u):!0}function Pd(e,t,r){var o=!1,a=pn,u=t.contextType;return typeof u=="object"&&u!==null?u=At(u):(a=ct(t)?_n:tt.current,o=t.contextTypes,u=(o=o!=null)?ur(e,a):pn),t=new t(r,u),e.memoizedState=t.state!==null&&t.state!==void 0?t.state:null,t.updater=Mi,e.stateNode=t,t._reactInternals=e,o&&(e=e.stateNode,e.__reactInternalMemoizedUnmaskedChildContext=a,e.__reactInternalMemoizedMaskedChildContext=u),t}function Md(e,t,r,o){e=t.state,typeof t.componentWillReceiveProps=="function"&&t.componentWillReceiveProps(r,o),typeof t.UNSAFE_componentWillReceiveProps=="function"&&t.UNSAFE_componentWillReceiveProps(r,o),t.state!==e&&Mi.enqueueReplaceState(t,t.state,null)}function Jl(e,t,r,o){var a=e.stateNode;a.props=r,a.state=e.memoizedState,a.refs={},bl(e);var u=t.contextType;typeof u=="object"&&u!==null?a.context=At(u):(u=ct(t)?_n:tt.current,a.context=ur(e,u)),a.state=e.memoizedState,u=t.getDerivedStateFromProps,typeof u=="function"&&(Xl(e,t,u,r),a.state=e.memoizedState),typeof t.getDerivedStateFromProps=="function"||typeof a.getSnapshotBeforeUpdate=="function"||typeof a.UNSAFE_componentWillMount!="function"&&typeof a.componentWillMount!="function"||(t=a.state,typeof a.componentWillMount=="function"&&a.componentWillMount(),typeof a.UNSAFE_componentWillMount=="function"&&a.UNSAFE_componentWillMount(),t!==a.state&&Mi.enqueueReplaceState(a,a.state,null),ki(e,r,a,o),a.state=e.memoizedState),typeof a.componentDidMount=="function"&&(e.flags|=4194308)}function yr(e,t){try{var r="",o=t;do r+=de(o),o=o.return;while(o);var a=r}catch(u){a=`
Error generating stack: `+u.message+`
`+u.stack}return{value:e,source:t,stack:a,digest:null}}function Zl(e,t,r){return{value:e,source:null,stack:r??null,digest:t??null}}function ea(e,t){try{console.error(t.value)}catch(r){setTimeout(function(){throw r})}}var wg=typeof WeakMap=="function"?WeakMap:Map;function _d(e,t,r){r=Zt(-1,r),r.tag=3,r.payload={element:null};var o=t.value;return r.callback=function(){Di||(Di=!0,ma=o),ea(e,t)},r}function Td(e,t,r){r=Zt(-1,r),r.tag=3;var o=e.type.getDerivedStateFromError;if(typeof o=="function"){var a=t.value;r.payload=function(){return o(a)},r.callback=function(){ea(e,t)}}var u=e.stateNode;return u!==null&&typeof u.componentDidCatch=="function"&&(r.callback=function(){ea(e,t),typeof o!="function"&&(xn===null?xn=new Set([this]):xn.add(this));var p=t.stack;this.componentDidCatch(t.value,{componentStack:p!==null?p:""})}),r}function Nd(e,t,r){var o=e.pingCache;if(o===null){o=e.pingCache=new wg;var a=new Set;o.set(t,a)}else a=o.get(t),a===void 0&&(a=new Set,o.set(t,a));a.has(r)||(a.add(r),e=Og.bind(null,e,t,r),t.then(e,e))}function $d(e){do{var t;if((t=e.tag===13)&&(t=e.memoizedState,t=t!==null?t.dehydrated!==null:!0),t)return e;e=e.return}while(e!==null);return null}function Od(e,t,r,o,a){return e.mode&1?(e.flags|=65536,e.lanes=a,e):(e===t?e.flags|=65536:(e.flags|=128,r.flags|=131072,r.flags&=-52805,r.tag===1&&(r.alternate===null?r.tag=17:(t=Zt(-1,1),t.tag=2,gn(r,t,1))),r.lanes|=1),e)}var Sg=X.ReactCurrentOwner,dt=!1;function lt(e,t,r,o){t.child=e===null?ed(t,null,r,o):pr(t,e.child,r,o)}function Ld(e,t,r,o,a){r=r.render;var u=t.ref;return mr(t,a),o=Vl(e,t,r,o,u,a),r=Wl(),e!==null&&!dt?(t.updateQueue=e.updateQueue,t.flags&=-2053,e.lanes&=~a,en(e,t,a)):(Ne&&r&&Pl(t),t.flags|=1,lt(e,t,o,a),t.child)}function Dd(e,t,r,o,a){if(e===null){var u=r.type;return typeof u=="function"&&!ka(u)&&u.defaultProps===void 0&&r.compare===null&&r.defaultProps===void 0?(t.tag=15,t.type=u,Id(e,t,u,o,a)):(e=Ui(r.type,null,o,t,t.mode,a),e.ref=t.ref,e.return=t,t.child=e)}if(u=e.child,!(e.lanes&a)){var p=u.memoizedProps;if(r=r.compare,r=r!==null?r:to,r(p,o)&&e.ref===t.ref)return en(e,t,a)}return t.flags|=1,e=kn(u,o),e.ref=t.ref,e.return=t,t.child=e}function Id(e,t,r,o,a){if(e!==null){var u=e.memoizedProps;if(to(u,o)&&e.ref===t.ref)if(dt=!1,t.pendingProps=o=u,(e.lanes&a)!==0)e.flags&131072&&(dt=!0);else return t.lanes=e.lanes,en(e,t,a)}return ta(e,t,r,o,a)}function bd(e,t,r){var o=t.pendingProps,a=o.children,u=e!==null?e.memoizedState:null;if(o.mode==="hidden")if(!(t.mode&1))t.memoizedState={baseLanes:0,cachePool:null,transitions:null},Pe(vr,vt),vt|=r;else{if(!(r&1073741824))return e=u!==null?u.baseLanes|r:r,t.lanes=t.childLanes=1073741824,t.memoizedState={baseLanes:e,cachePool:null,transitions:null},t.updateQueue=null,Pe(vr,vt),vt|=e,null;t.memoizedState={baseLanes:0,cachePool:null,transitions:null},o=u!==null?u.baseLanes:r,Pe(vr,vt),vt|=o}else u!==null?(o=u.baseLanes|r,t.memoizedState=null):o=r,Pe(vr,vt),vt|=o;return lt(e,t,a,r),t.child}function zd(e,t){var r=t.ref;(e===null&&r!==null||e!==null&&e.ref!==r)&&(t.flags|=512,t.flags|=2097152)}function ta(e,t,r,o,a){var u=ct(r)?_n:tt.current;return u=ur(t,u),mr(t,a),r=Vl(e,t,r,o,u,a),o=Wl(),e!==null&&!dt?(t.updateQueue=e.updateQueue,t.flags&=-2053,e.lanes&=~a,en(e,t,a)):(Ne&&o&&Pl(t),t.flags|=1,lt(e,t,r,a),t.child)}function Bd(e,t,r,o,a){if(ct(r)){var u=!0;pi(t)}else u=!1;if(mr(t,a),t.stateNode===null)Ti(e,t),Pd(t,r,o),Jl(t,r,o,a),o=!0;else if(e===null){var p=t.stateNode,g=t.memoizedProps;p.props=g;var v=p.context,T=r.contextType;typeof T=="object"&&T!==null?T=At(T):(T=ct(r)?_n:tt.current,T=ur(t,T));var Y=r.getDerivedStateFromProps,q=typeof Y=="function"||typeof p.getSnapshotBeforeUpdate=="function";q||typeof p.UNSAFE_componentWillReceiveProps!="function"&&typeof p.componentWillReceiveProps!="function"||(g!==o||v!==T)&&Md(t,p,o,T),mn=!1;var U=t.memoizedState;p.state=U,ki(t,o,p,a),v=t.memoizedState,g!==o||U!==v||ut.current||mn?(typeof Y=="function"&&(Xl(t,r,Y,o),v=t.memoizedState),(g=mn||Rd(t,r,g,o,U,v,T))?(q||typeof p.UNSAFE_componentWillMount!="function"&&typeof p.componentWillMount!="function"||(typeof p.componentWillMount=="function"&&p.componentWillMount(),typeof p.UNSAFE_componentWillMount=="function"&&p.UNSAFE_componentWillMount()),typeof p.componentDidMount=="function"&&(t.flags|=4194308)):(typeof p.componentDidMount=="function"&&(t.flags|=4194308),t.memoizedProps=o,t.memoizedState=v),p.props=o,p.state=v,p.context=T,o=g):(typeof p.componentDidMount=="function"&&(t.flags|=4194308),o=!1)}else{p=t.stateNode,nd(e,t),g=t.memoizedProps,T=t.type===t.elementType?g:Ot(t.type,g),p.props=T,q=t.pendingProps,U=p.context,v=r.contextType,typeof v=="object"&&v!==null?v=At(v):(v=ct(r)?_n:tt.current,v=ur(t,v));var te=r.getDerivedStateFromProps;(Y=typeof te=="function"||typeof p.getSnapshotBeforeUpdate=="function")||typeof p.UNSAFE_componentWillReceiveProps!="function"&&typeof p.componentWillReceiveProps!="function"||(g!==q||U!==v)&&Md(t,p,o,v),mn=!1,U=t.memoizedState,p.state=U,ki(t,o,p,a);var re=t.memoizedState;g!==q||U!==re||ut.current||mn?(typeof te=="function"&&(Xl(t,r,te,o),re=t.memoizedState),(T=mn||Rd(t,r,T,o,U,re,v)||!1)?(Y||typeof p.UNSAFE_componentWillUpdate!="function"&&typeof p.componentWillUpdate!="function"||(typeof p.componentWillUpdate=="function"&&p.componentWillUpdate(o,re,v),typeof p.UNSAFE_componentWillUpdate=="function"&&p.UNSAFE_componentWillUpdate(o,re,v)),typeof p.componentDidUpdate=="function"&&(t.flags|=4),typeof p.getSnapshotBeforeUpdate=="function"&&(t.flags|=1024)):(typeof p.componentDidUpdate!="function"||g===e.memoizedProps&&U===e.memoizedState||(t.flags|=4),typeof p.getSnapshotBeforeUpdate!="function"||g===e.memoizedProps&&U===e.memoizedState||(t.flags|=1024),t.memoizedProps=o,t.memoizedState=re),p.props=o,p.state=re,p.context=v,o=T):(typeof p.componentDidUpdate!="function"||g===e.memoizedProps&&U===e.memoizedState||(t.flags|=4),typeof p.getSnapshotBeforeUpdate!="function"||g===e.memoizedProps&&U===e.memoizedState||(t.flags|=1024),o=!1)}return na(e,t,r,o,u,a)}function na(e,t,r,o,a,u){zd(e,t);var p=(t.flags&128)!==0;if(!o&&!p)return a&&Vc(t,r,!1),en(e,t,u);o=t.stateNode,Sg.current=t;var g=p&&typeof r.getDerivedStateFromError!="function"?null:o.render();return t.flags|=1,e!==null&&p?(t.child=pr(t,e.child,null,u),t.child=pr(t,null,g,u)):lt(e,t,g,u),t.memoizedState=o.state,a&&Vc(t,r,!0),t.child}function Fd(e){var t=e.stateNode;t.pendingContext?Hc(e,t.pendingContext,t.pendingContext!==t.context):t.context&&Hc(e,t.context,!1),zl(e,t.containerInfo)}function Ud(e,t,r,o,a){return fr(),Nl(a),t.flags|=256,lt(e,t,r,o),t.child}var ra={dehydrated:null,treeContext:null,retryLane:0};function oa(e){return{baseLanes:e,cachePool:null,transitions:null}}function Hd(e,t,r){var o=t.pendingProps,a=$e.current,u=!1,p=(t.flags&128)!==0,g;if((g=p)||(g=e!==null&&e.memoizedState===null?!1:(a&2)!==0),g?(u=!0,t.flags&=-129):(e===null||e.memoizedState!==null)&&(a|=1),Pe($e,a&1),e===null)return Tl(t),e=t.memoizedState,e!==null&&(e=e.dehydrated,e!==null)?(t.mode&1?e.data==="$!"?t.lanes=8:t.lanes=1073741824:t.lanes=1,null):(p=o.children,e=o.fallback,u?(o=t.mode,u=t.child,p={mode:"hidden",children:p},!(o&1)&&u!==null?(u.childLanes=0,u.pendingProps=p):u=Hi(p,o,0,null),e=Bn(e,o,r,null),u.return=t,e.return=t,u.sibling=e,t.child=u,t.child.memoizedState=oa(r),t.memoizedState=ra,e):ia(t,p));if(a=e.memoizedState,a!==null&&(g=a.dehydrated,g!==null))return kg(e,t,p,o,g,a,r);if(u){u=o.fallback,p=t.mode,a=e.child,g=a.sibling;var v={mode:"hidden",children:o.children};return!(p&1)&&t.child!==a?(o=t.child,o.childLanes=0,o.pendingProps=v,t.deletions=null):(o=kn(a,v),o.subtreeFlags=a.subtreeFlags&14680064),g!==null?u=kn(g,u):(u=Bn(u,p,r,null),u.flags|=2),u.return=t,o.return=t,o.sibling=u,t.child=o,o=u,u=t.child,p=e.child.memoizedState,p=p===null?oa(r):{baseLanes:p.baseLanes|r,cachePool:null,transitions:p.transitions},u.memoizedState=p,u.childLanes=e.childLanes&~r,t.memoizedState=ra,o}return u=e.child,e=u.sibling,o=kn(u,{mode:"visible",children:o.children}),!(t.mode&1)&&(o.lanes=r),o.return=t,o.sibling=null,e!==null&&(r=t.deletions,r===null?(t.deletions=[e],t.flags|=16):r.push(e)),t.child=o,t.memoizedState=null,o}function ia(e,t){return t=Hi({mode:"visible",children:t},e.mode,0,null),t.return=e,e.child=t}function _i(e,t,r,o){return o!==null&&Nl(o),pr(t,e.child,null,r),e=ia(t,t.pendingProps.children),e.flags|=2,t.memoizedState=null,e}function kg(e,t,r,o,a,u,p){if(r)return t.flags&256?(t.flags&=-257,o=Zl(Error(s(422))),_i(e,t,p,o)):t.memoizedState!==null?(t.child=e.child,t.flags|=128,null):(u=o.fallback,a=t.mode,o=Hi({mode:"visible",children:o.children},a,0,null),u=Bn(u,a,p,null),u.flags|=2,o.return=t,u.return=t,o.sibling=u,t.child=o,t.mode&1&&pr(t,e.child,null,p),t.child.memoizedState=oa(p),t.memoizedState=ra,u);if(!(t.mode&1))return _i(e,t,p,null);if(a.data==="$!"){if(o=a.nextSibling&&a.nextSibling.dataset,o)var g=o.dgst;return o=g,u=Error(s(419)),o=Zl(u,o,void 0),_i(e,t,p,o)}if(g=(p&e.childLanes)!==0,dt||g){if(o=Qe,o!==null){switch(p&-p){case 4:a=2;break;case 16:a=8;break;case 64:case 128:case 256:case 512:case 1024:case 2048:case 4096:case 8192:case 16384:case 32768:case 65536:case 131072:case 262144:case 524288:case 1048576:case 2097152:case 4194304:case 8388608:case 16777216:case 33554432:case 67108864:a=32;break;case 536870912:a=268435456;break;default:a=0}a=a&(o.suspendedLanes|p)?0:a,a!==0&&a!==u.retryLane&&(u.retryLane=a,Jt(e,a),It(o,e,a,-1))}return Sa(),o=Zl(Error(s(421))),_i(e,t,p,o)}return a.data==="$?"?(t.flags|=128,t.child=e.child,t=Lg.bind(null,e),a._reactRetry=t,null):(e=u.treeContext,xt=dn(a.nextSibling),yt=t,Ne=!0,$t=null,e!==null&&(Et[jt++]=Kt,Et[jt++]=Xt,Et[jt++]=Tn,Kt=e.id,Xt=e.overflow,Tn=t),t=ia(t,o.children),t.flags|=4096,t)}function Yd(e,t,r){e.lanes|=t;var o=e.alternate;o!==null&&(o.lanes|=t),Dl(e.return,t,r)}function sa(e,t,r,o,a){var u=e.memoizedState;u===null?e.memoizedState={isBackwards:t,rendering:null,renderingStartTime:0,last:o,tail:r,tailMode:a}:(u.isBackwards=t,u.rendering=null,u.renderingStartTime=0,u.last=o,u.tail=r,u.tailMode=a)}function Vd(e,t,r){var o=t.pendingProps,a=o.revealOrder,u=o.tail;if(lt(e,t,o.children,r),o=$e.current,o&2)o=o&1|2,t.flags|=128;else{if(e!==null&&e.flags&128)e:for(e=t.child;e!==null;){if(e.tag===13)e.memoizedState!==null&&Yd(e,r,t);else if(e.tag===19)Yd(e,r,t);else if(e.child!==null){e.child.return=e,e=e.child;continue}if(e===t)break e;for(;e.sibling===null;){if(e.return===null||e.return===t)break e;e=e.return}e.sibling.return=e.return,e=e.sibling}o&=1}if(Pe($e,o),!(t.mode&1))t.memoizedState=null;else switch(a){case"forwards":for(r=t.child,a=null;r!==null;)e=r.alternate,e!==null&&Ci(e)===null&&(a=r),r=r.sibling;r=a,r===null?(a=t.child,t.child=null):(a=r.sibling,r.sibling=null),sa(t,!1,a,r,u);break;case"backwards":for(r=null,a=t.child,t.child=null;a!==null;){if(e=a.alternate,e!==null&&Ci(e)===null){t.child=a;break}e=a.sibling,a.sibling=r,r=a,a=e}sa(t,!0,r,null,u);break;case"together":sa(t,!1,null,null,void 0);break;default:t.memoizedState=null}return t.child}function Ti(e,t){!(t.mode&1)&&e!==null&&(e.alternate=null,t.alternate=null,t.flags|=2)}function en(e,t,r){if(e!==null&&(t.dependencies=e.dependencies),Dn|=t.lanes,!(r&t.childLanes))return null;if(e!==null&&t.child!==e.child)throw Error(s(153));if(t.child!==null){for(e=t.child,r=kn(e,e.pendingProps),t.child=r,r.return=t;e.sibling!==null;)e=e.sibling,r=r.sibling=kn(e,e.pendingProps),r.return=t;r.sibling=null}return t.child}function Cg(e,t,r){switch(t.tag){case 3:Fd(t),fr();break;case 5:id(t);break;case 1:ct(t.type)&&pi(t);break;case 4:zl(t,t.stateNode.containerInfo);break;case 10:var o=t.type._context,a=t.memoizedProps.value;Pe(vi,o._currentValue),o._currentValue=a;break;case 13:if(o=t.memoizedState,o!==null)return o.dehydrated!==null?(Pe($e,$e.current&1),t.flags|=128,null):r&t.child.childLanes?Hd(e,t,r):(Pe($e,$e.current&1),e=en(e,t,r),e!==null?e.sibling:null);Pe($e,$e.current&1);break;case 19:if(o=(r&t.childLanes)!==0,e.flags&128){if(o)return Vd(e,t,r);t.flags|=128}if(a=t.memoizedState,a!==null&&(a.rendering=null,a.tail=null,a.lastEffect=null),Pe($e,$e.current),o)break;return null;case 22:case 23:return t.lanes=0,bd(e,t,r)}return en(e,t,r)}var Wd,la,qd,Qd;Wd=function(e,t){for(var r=t.child;r!==null;){if(r.tag===5||r.tag===6)e.appendChild(r.stateNode);else if(r.tag!==4&&r.child!==null){r.child.return=r,r=r.child;continue}if(r===t)break;for(;r.sibling===null;){if(r.return===null||r.return===t)return;r=r.return}r.sibling.return=r.return,r=r.sibling}},la=function(){},qd=function(e,t,r,o){var a=e.memoizedProps;if(a!==o){e=t.stateNode,On(Ut.current);var u=null;switch(r){case"input":a=Ds(e,a),o=Ds(e,o),u=[];break;case"select":a=B({},a,{value:void 0}),o=B({},o,{value:void 0}),u=[];break;case"textarea":a=zs(e,a),o=zs(e,o),u=[];break;default:typeof a.onClick!="function"&&typeof o.onClick=="function"&&(e.onclick=ci)}Fs(r,o);var p;r=null;for(T in a)if(!o.hasOwnProperty(T)&&a.hasOwnProperty(T)&&a[T]!=null)if(T==="style"){var g=a[T];for(p in g)g.hasOwnProperty(p)&&(r||(r={}),r[p]="")}else T!=="dangerouslySetInnerHTML"&&T!=="children"&&T!=="suppressContentEditableWarning"&&T!=="suppressHydrationWarning"&&T!=="autoFocus"&&(c.hasOwnProperty(T)?u||(u=[]):(u=u||[]).push(T,null));for(T in o){var v=o[T];if(g=a!=null?a[T]:void 0,o.hasOwnProperty(T)&&v!==g&&(v!=null||g!=null))if(T==="style")if(g){for(p in g)!g.hasOwnProperty(p)||v&&v.hasOwnProperty(p)||(r||(r={}),r[p]="");for(p in v)v.hasOwnProperty(p)&&g[p]!==v[p]&&(r||(r={}),r[p]=v[p])}else r||(u||(u=[]),u.push(T,r)),r=v;else T==="dangerouslySetInnerHTML"?(v=v?v.__html:void 0,g=g?g.__html:void 0,v!=null&&g!==v&&(u=u||[]).push(T,v)):T==="children"?typeof v!="string"&&typeof v!="number"||(u=u||[]).push(T,""+v):T!=="suppressContentEditableWarning"&&T!=="suppressHydrationWarning"&&(c.hasOwnProperty(T)?(v!=null&&T==="onScroll"&&Me("scroll",e),u||g===v||(u=[])):(u=u||[]).push(T,v))}r&&(u=u||[]).push("style",r);var T=u;(t.updateQueue=T)&&(t.flags|=4)}},Qd=function(e,t,r,o){r!==o&&(t.flags|=4)};function yo(e,t){if(!Ne)switch(e.tailMode){case"hidden":t=e.tail;for(var r=null;t!==null;)t.alternate!==null&&(r=t),t=t.sibling;r===null?e.tail=null:r.sibling=null;break;case"collapsed":r=e.tail;for(var o=null;r!==null;)r.alternate!==null&&(o=r),r=r.sibling;o===null?t||e.tail===null?e.tail=null:e.tail.sibling=null:o.sibling=null}}function rt(e){var t=e.alternate!==null&&e.alternate.child===e.child,r=0,o=0;if(t)for(var a=e.child;a!==null;)r|=a.lanes|a.childLanes,o|=a.subtreeFlags&14680064,o|=a.flags&14680064,a.return=e,a=a.sibling;else for(a=e.child;a!==null;)r|=a.lanes|a.childLanes,o|=a.subtreeFlags,o|=a.flags,a.return=e,a=a.sibling;return e.subtreeFlags|=o,e.childLanes=r,t}function Eg(e,t,r){var o=t.pendingProps;switch(Ml(t),t.tag){case 2:case 16:case 15:case 0:case 11:case 7:case 8:case 12:case 9:case 14:return rt(t),null;case 1:return ct(t.type)&&fi(),rt(t),null;case 3:return o=t.stateNode,gr(),_e(ut),_e(tt),Ul(),o.pendingContext&&(o.context=o.pendingContext,o.pendingContext=null),(e===null||e.child===null)&&(yi(t)?t.flags|=4:e===null||e.memoizedState.isDehydrated&&!(t.flags&256)||(t.flags|=1024,$t!==null&&(xa($t),$t=null))),la(e,t),rt(t),null;case 5:Bl(t);var a=On(fo.current);if(r=t.type,e!==null&&t.stateNode!=null)qd(e,t,r,o,a),e.ref!==t.ref&&(t.flags|=512,t.flags|=2097152);else{if(!o){if(t.stateNode===null)throw Error(s(166));return rt(t),null}if(e=On(Ut.current),yi(t)){o=t.stateNode,r=t.type;var u=t.memoizedProps;switch(o[Ft]=t,o[so]=u,e=(t.mode&1)!==0,r){case"dialog":Me("cancel",o),Me("close",o);break;case"iframe":case"object":case"embed":Me("load",o);break;case"video":case"audio":for(a=0;a<ro.length;a++)Me(ro[a],o);break;case"source":Me("error",o);break;case"img":case"image":case"link":Me("error",o),Me("load",o);break;case"details":Me("toggle",o);break;case"input":Pu(o,u),Me("invalid",o);break;case"select":o._wrapperState={wasMultiple:!!u.multiple},Me("invalid",o);break;case"textarea":Tu(o,u),Me("invalid",o)}Fs(r,u),a=null;for(var p in u)if(u.hasOwnProperty(p)){var g=u[p];p==="children"?typeof g=="string"?o.textContent!==g&&(u.suppressHydrationWarning!==!0&&ui(o.textContent,g,e),a=["children",g]):typeof g=="number"&&o.textContent!==""+g&&(u.suppressHydrationWarning!==!0&&ui(o.textContent,g,e),a=["children",""+g]):c.hasOwnProperty(p)&&g!=null&&p==="onScroll"&&Me("scroll",o)}switch(r){case"input":We(o),_u(o,u,!0);break;case"textarea":We(o),$u(o);break;case"select":case"option":break;default:typeof u.onClick=="function"&&(o.onclick=ci)}o=a,t.updateQueue=o,o!==null&&(t.flags|=4)}else{p=a.nodeType===9?a:a.ownerDocument,e==="http://www.w3.org/1999/xhtml"&&(e=Ou(r)),e==="http://www.w3.org/1999/xhtml"?r==="script"?(e=p.createElement("div"),e.innerHTML="<script><\/script>",e=e.removeChild(e.firstChild)):typeof o.is=="string"?e=p.createElement(r,{is:o.is}):(e=p.createElement(r),r==="select"&&(p=e,o.multiple?p.multiple=!0:o.size&&(p.size=o.size))):e=p.createElementNS(e,r),e[Ft]=t,e[so]=o,Wd(e,t,!1,!1),t.stateNode=e;e:{switch(p=Us(r,o),r){case"dialog":Me("cancel",e),Me("close",e),a=o;break;case"iframe":case"object":case"embed":Me("load",e),a=o;break;case"video":case"audio":for(a=0;a<ro.length;a++)Me(ro[a],e);a=o;break;case"source":Me("error",e),a=o;break;case"img":case"image":case"link":Me("error",e),Me("load",e),a=o;break;case"details":Me("toggle",e),a=o;break;case"input":Pu(e,o),a=Ds(e,o),Me("invalid",e);break;case"option":a=o;break;case"select":e._wrapperState={wasMultiple:!!o.multiple},a=B({},o,{value:void 0}),Me("invalid",e);break;case"textarea":Tu(e,o),a=zs(e,o),Me("invalid",e);break;default:a=o}Fs(r,a),g=a;for(u in g)if(g.hasOwnProperty(u)){var v=g[u];u==="style"?Iu(e,v):u==="dangerouslySetInnerHTML"?(v=v?v.__html:void 0,v!=null&&Lu(e,v)):u==="children"?typeof v=="string"?(r!=="textarea"||v!=="")&&br(e,v):typeof v=="number"&&br(e,""+v):u!=="suppressContentEditableWarning"&&u!=="suppressHydrationWarning"&&u!=="autoFocus"&&(c.hasOwnProperty(u)?v!=null&&u==="onScroll"&&Me("scroll",e):v!=null&&H(e,u,v,p))}switch(r){case"input":We(e),_u(e,o,!1);break;case"textarea":We(e),$u(e);break;case"option":o.value!=null&&e.setAttribute("value",""+ye(o.value));break;case"select":e.multiple=!!o.multiple,u=o.value,u!=null?Xn(e,!!o.multiple,u,!1):o.defaultValue!=null&&Xn(e,!!o.multiple,o.defaultValue,!0);break;default:typeof a.onClick=="function"&&(e.onclick=ci)}switch(r){case"button":case"input":case"select":case"textarea":o=!!o.autoFocus;break e;case"img":o=!0;break e;default:o=!1}}o&&(t.flags|=4)}t.ref!==null&&(t.flags|=512,t.flags|=2097152)}return rt(t),null;case 6:if(e&&t.stateNode!=null)Qd(e,t,e.memoizedProps,o);else{if(typeof o!="string"&&t.stateNode===null)throw Error(s(166));if(r=On(fo.current),On(Ut.current),yi(t)){if(o=t.stateNode,r=t.memoizedProps,o[Ft]=t,(u=o.nodeValue!==r)&&(e=yt,e!==null))switch(e.tag){case 3:ui(o.nodeValue,r,(e.mode&1)!==0);break;case 5:e.memoizedProps.suppressHydrationWarning!==!0&&ui(o.nodeValue,r,(e.mode&1)!==0)}u&&(t.flags|=4)}else o=(r.nodeType===9?r:r.ownerDocument).createTextNode(o),o[Ft]=t,t.stateNode=o}return rt(t),null;case 13:if(_e($e),o=t.memoizedState,e===null||e.memoizedState!==null&&e.memoizedState.dehydrated!==null){if(Ne&&xt!==null&&t.mode&1&&!(t.flags&128))Xc(),fr(),t.flags|=98560,u=!1;else if(u=yi(t),o!==null&&o.dehydrated!==null){if(e===null){if(!u)throw Error(s(318));if(u=t.memoizedState,u=u!==null?u.dehydrated:null,!u)throw Error(s(317));u[Ft]=t}else fr(),!(t.flags&128)&&(t.memoizedState=null),t.flags|=4;rt(t),u=!1}else $t!==null&&(xa($t),$t=null),u=!0;if(!u)return t.flags&65536?t:null}return t.flags&128?(t.lanes=r,t):(o=o!==null,o!==(e!==null&&e.memoizedState!==null)&&o&&(t.child.flags|=8192,t.mode&1&&(e===null||$e.current&1?Ve===0&&(Ve=3):Sa())),t.updateQueue!==null&&(t.flags|=4),rt(t),null);case 4:return gr(),la(e,t),e===null&&oo(t.stateNode.containerInfo),rt(t),null;case 10:return Ll(t.type._context),rt(t),null;case 17:return ct(t.type)&&fi(),rt(t),null;case 19:if(_e($e),u=t.memoizedState,u===null)return rt(t),null;if(o=(t.flags&128)!==0,p=u.rendering,p===null)if(o)yo(u,!1);else{if(Ve!==0||e!==null&&e.flags&128)for(e=t.child;e!==null;){if(p=Ci(e),p!==null){for(t.flags|=128,yo(u,!1),o=p.updateQueue,o!==null&&(t.updateQueue=o,t.flags|=4),t.subtreeFlags=0,o=r,r=t.child;r!==null;)u=r,e=o,u.flags&=14680066,p=u.alternate,p===null?(u.childLanes=0,u.lanes=e,u.child=null,u.subtreeFlags=0,u.memoizedProps=null,u.memoizedState=null,u.updateQueue=null,u.dependencies=null,u.stateNode=null):(u.childLanes=p.childLanes,u.lanes=p.lanes,u.child=p.child,u.subtreeFlags=0,u.deletions=null,u.memoizedProps=p.memoizedProps,u.memoizedState=p.memoizedState,u.updateQueue=p.updateQueue,u.type=p.type,e=p.dependencies,u.dependencies=e===null?null:{lanes:e.lanes,firstContext:e.firstContext}),r=r.sibling;return Pe($e,$e.current&1|2),t.child}e=e.sibling}u.tail!==null&&Ie()>wr&&(t.flags|=128,o=!0,yo(u,!1),t.lanes=4194304)}else{if(!o)if(e=Ci(p),e!==null){if(t.flags|=128,o=!0,r=e.updateQueue,r!==null&&(t.updateQueue=r,t.flags|=4),yo(u,!0),u.tail===null&&u.tailMode==="hidden"&&!p.alternate&&!Ne)return rt(t),null}else 2*Ie()-u.renderingStartTime>wr&&r!==1073741824&&(t.flags|=128,o=!0,yo(u,!1),t.lanes=4194304);u.isBackwards?(p.sibling=t.child,t.child=p):(r=u.last,r!==null?r.sibling=p:t.child=p,u.last=p)}return u.tail!==null?(t=u.tail,u.rendering=t,u.tail=t.sibling,u.renderingStartTime=Ie(),t.sibling=null,r=$e.current,Pe($e,o?r&1|2:r&1),t):(rt(t),null);case 22:case 23:return wa(),o=t.memoizedState!==null,e!==null&&e.memoizedState!==null!==o&&(t.flags|=8192),o&&t.mode&1?vt&1073741824&&(rt(t),t.subtreeFlags&6&&(t.flags|=8192)):rt(t),null;case 24:return null;case 25:return null}throw Error(s(156,t.tag))}function jg(e,t){switch(Ml(t),t.tag){case 1:return ct(t.type)&&fi(),e=t.flags,e&65536?(t.flags=e&-65537|128,t):null;case 3:return gr(),_e(ut),_e(tt),Ul(),e=t.flags,e&65536&&!(e&128)?(t.flags=e&-65537|128,t):null;case 5:return Bl(t),null;case 13:if(_e($e),e=t.memoizedState,e!==null&&e.dehydrated!==null){if(t.alternate===null)throw Error(s(340));fr()}return e=t.flags,e&65536?(t.flags=e&-65537|128,t):null;case 19:return _e($e),null;case 4:return gr(),null;case 10:return Ll(t.type._context),null;case 22:case 23:return wa(),null;case 24:return null;default:return null}}var Ni=!1,ot=!1,Ag=typeof WeakSet=="function"?WeakSet:Set,ne=null;function xr(e,t){var r=e.ref;if(r!==null)if(typeof r=="function")try{r(null)}catch(o){De(e,t,o)}else r.current=null}function aa(e,t,r){try{r()}catch(o){De(e,t,o)}}var Gd=!1;function Rg(e,t){if(wl=Jo,e=Rc(),fl(e)){if("selectionStart"in e)var r={start:e.selectionStart,end:e.selectionEnd};else e:{r=(r=e.ownerDocument)&&r.defaultView||window;var o=r.getSelection&&r.getSelection();if(o&&o.rangeCount!==0){r=o.anchorNode;var a=o.anchorOffset,u=o.focusNode;o=o.focusOffset;try{r.nodeType,u.nodeType}catch{r=null;break e}var p=0,g=-1,v=-1,T=0,Y=0,q=e,U=null;t:for(;;){for(var te;q!==r||a!==0&&q.nodeType!==3||(g=p+a),q!==u||o!==0&&q.nodeType!==3||(v=p+o),q.nodeType===3&&(p+=q.nodeValue.length),(te=q.firstChild)!==null;)U=q,q=te;for(;;){if(q===e)break t;if(U===r&&++T===a&&(g=p),U===u&&++Y===o&&(v=p),(te=q.nextSibling)!==null)break;q=U,U=q.parentNode}q=te}r=g===-1||v===-1?null:{start:g,end:v}}else r=null}r=r||{start:0,end:0}}else r=null;for(Sl={focusedElem:e,selectionRange:r},Jo=!1,ne=t;ne!==null;)if(t=ne,e=t.child,(t.subtreeFlags&1028)!==0&&e!==null)e.return=t,ne=e;else for(;ne!==null;){t=ne;try{var re=t.alternate;if(t.flags&1024)switch(t.tag){case 0:case 11:case 15:break;case 1:if(re!==null){var oe=re.memoizedProps,be=re.memoizedState,R=t.stateNode,C=R.getSnapshotBeforeUpdate(t.elementType===t.type?oe:Ot(t.type,oe),be);R.__reactInternalSnapshotBeforeUpdate=C}break;case 3:var M=t.stateNode.containerInfo;M.nodeType===1?M.textContent="":M.nodeType===9&&M.documentElement&&M.removeChild(M.documentElement);break;case 5:case 6:case 4:case 17:break;default:throw Error(s(163))}}catch(K){De(t,t.return,K)}if(e=t.sibling,e!==null){e.return=t.return,ne=e;break}ne=t.return}return re=Gd,Gd=!1,re}function xo(e,t,r){var o=t.updateQueue;if(o=o!==null?o.lastEffect:null,o!==null){var a=o=o.next;do{if((a.tag&e)===e){var u=a.destroy;a.destroy=void 0,u!==void 0&&aa(t,r,u)}a=a.next}while(a!==o)}}function $i(e,t){if(t=t.updateQueue,t=t!==null?t.lastEffect:null,t!==null){var r=t=t.next;do{if((r.tag&e)===e){var o=r.create;r.destroy=o()}r=r.next}while(r!==t)}}function ua(e){var t=e.ref;if(t!==null){var r=e.stateNode;switch(e.tag){case 5:e=r;break;default:e=r}typeof t=="function"?t(e):t.current=e}}function Kd(e){var t=e.alternate;t!==null&&(e.alternate=null,Kd(t)),e.child=null,e.deletions=null,e.sibling=null,e.tag===5&&(t=e.stateNode,t!==null&&(delete t[Ft],delete t[so],delete t[jl],delete t[ug],delete t[cg])),e.stateNode=null,e.return=null,e.dependencies=null,e.memoizedProps=null,e.memoizedState=null,e.pendingProps=null,e.stateNode=null,e.updateQueue=null}function Xd(e){return e.tag===5||e.tag===3||e.tag===4}function Jd(e){e:for(;;){for(;e.sibling===null;){if(e.return===null||Xd(e.return))return null;e=e.return}for(e.sibling.return=e.return,e=e.sibling;e.tag!==5&&e.tag!==6&&e.tag!==18;){if(e.flags&2||e.child===null||e.tag===4)continue e;e.child.return=e,e=e.child}if(!(e.flags&2))return e.stateNode}}function ca(e,t,r){var o=e.tag;if(o===5||o===6)e=e.stateNode,t?r.nodeType===8?r.parentNode.insertBefore(e,t):r.insertBefore(e,t):(r.nodeType===8?(t=r.parentNode,t.insertBefore(e,r)):(t=r,t.appendChild(e)),r=r._reactRootContainer,r!=null||t.onclick!==null||(t.onclick=ci));else if(o!==4&&(e=e.child,e!==null))for(ca(e,t,r),e=e.sibling;e!==null;)ca(e,t,r),e=e.sibling}function da(e,t,r){var o=e.tag;if(o===5||o===6)e=e.stateNode,t?r.insertBefore(e,t):r.appendChild(e);else if(o!==4&&(e=e.child,e!==null))for(da(e,t,r),e=e.sibling;e!==null;)da(e,t,r),e=e.sibling}var Je=null,Lt=!1;function yn(e,t,r){for(r=r.child;r!==null;)Zd(e,t,r),r=r.sibling}function Zd(e,t,r){if(Bt&&typeof Bt.onCommitFiberUnmount=="function")try{Bt.onCommitFiberUnmount(Wo,r)}catch{}switch(r.tag){case 5:ot||xr(r,t);case 6:var o=Je,a=Lt;Je=null,yn(e,t,r),Je=o,Lt=a,Je!==null&&(Lt?(e=Je,r=r.stateNode,e.nodeType===8?e.parentNode.removeChild(r):e.removeChild(r)):Je.removeChild(r.stateNode));break;case 18:Je!==null&&(Lt?(e=Je,r=r.stateNode,e.nodeType===8?El(e.parentNode,r):e.nodeType===1&&El(e,r),Gr(e)):El(Je,r.stateNode));break;case 4:o=Je,a=Lt,Je=r.stateNode.containerInfo,Lt=!0,yn(e,t,r),Je=o,Lt=a;break;case 0:case 11:case 14:case 15:if(!ot&&(o=r.updateQueue,o!==null&&(o=o.lastEffect,o!==null))){a=o=o.next;do{var u=a,p=u.destroy;u=u.tag,p!==void 0&&(u&2||u&4)&&aa(r,t,p),a=a.next}while(a!==o)}yn(e,t,r);break;case 1:if(!ot&&(xr(r,t),o=r.stateNode,typeof o.componentWillUnmount=="function"))try{o.props=r.memoizedProps,o.state=r.memoizedState,o.componentWillUnmount()}catch(g){De(r,t,g)}yn(e,t,r);break;case 21:yn(e,t,r);break;case 22:r.mode&1?(ot=(o=ot)||r.memoizedState!==null,yn(e,t,r),ot=o):yn(e,t,r);break;default:yn(e,t,r)}}function ef(e){var t=e.updateQueue;if(t!==null){e.updateQueue=null;var r=e.stateNode;r===null&&(r=e.stateNode=new Ag),t.forEach(function(o){var a=Dg.bind(null,e,o);r.has(o)||(r.add(o),o.then(a,a))})}}function Dt(e,t){var r=t.deletions;if(r!==null)for(var o=0;o<r.length;o++){var a=r[o];try{var u=e,p=t,g=p;e:for(;g!==null;){switch(g.tag){case 5:Je=g.stateNode,Lt=!1;break e;case 3:Je=g.stateNode.containerInfo,Lt=!0;break e;case 4:Je=g.stateNode.containerInfo,Lt=!0;break e}g=g.return}if(Je===null)throw Error(s(160));Zd(u,p,a),Je=null,Lt=!1;var v=a.alternate;v!==null&&(v.return=null),a.return=null}catch(T){De(a,t,T)}}if(t.subtreeFlags&12854)for(t=t.child;t!==null;)tf(t,e),t=t.sibling}function tf(e,t){var r=e.alternate,o=e.flags;switch(e.tag){case 0:case 11:case 14:case 15:if(Dt(t,e),Yt(e),o&4){try{xo(3,e,e.return),$i(3,e)}catch(oe){De(e,e.return,oe)}try{xo(5,e,e.return)}catch(oe){De(e,e.return,oe)}}break;case 1:Dt(t,e),Yt(e),o&512&&r!==null&&xr(r,r.return);break;case 5:if(Dt(t,e),Yt(e),o&512&&r!==null&&xr(r,r.return),e.flags&32){var a=e.stateNode;try{br(a,"")}catch(oe){De(e,e.return,oe)}}if(o&4&&(a=e.stateNode,a!=null)){var u=e.memoizedProps,p=r!==null?r.memoizedProps:u,g=e.type,v=e.updateQueue;if(e.updateQueue=null,v!==null)try{g==="input"&&u.type==="radio"&&u.name!=null&&Mu(a,u),Us(g,p);var T=Us(g,u);for(p=0;p<v.length;p+=2){var Y=v[p],q=v[p+1];Y==="style"?Iu(a,q):Y==="dangerouslySetInnerHTML"?Lu(a,q):Y==="children"?br(a,q):H(a,Y,q,T)}switch(g){case"input":Is(a,u);break;case"textarea":Nu(a,u);break;case"select":var U=a._wrapperState.wasMultiple;a._wrapperState.wasMultiple=!!u.multiple;var te=u.value;te!=null?Xn(a,!!u.multiple,te,!1):U!==!!u.multiple&&(u.defaultValue!=null?Xn(a,!!u.multiple,u.defaultValue,!0):Xn(a,!!u.multiple,u.multiple?[]:"",!1))}a[so]=u}catch(oe){De(e,e.return,oe)}}break;case 6:if(Dt(t,e),Yt(e),o&4){if(e.stateNode===null)throw Error(s(162));a=e.stateNode,u=e.memoizedProps;try{a.nodeValue=u}catch(oe){De(e,e.return,oe)}}break;case 3:if(Dt(t,e),Yt(e),o&4&&r!==null&&r.memoizedState.isDehydrated)try{Gr(t.containerInfo)}catch(oe){De(e,e.return,oe)}break;case 4:Dt(t,e),Yt(e);break;case 13:Dt(t,e),Yt(e),a=e.child,a.flags&8192&&(u=a.memoizedState!==null,a.stateNode.isHidden=u,!u||a.alternate!==null&&a.alternate.memoizedState!==null||(ha=Ie())),o&4&&ef(e);break;case 22:if(Y=r!==null&&r.memoizedState!==null,e.mode&1?(ot=(T=ot)||Y,Dt(t,e),ot=T):Dt(t,e),Yt(e),o&8192){if(T=e.memoizedState!==null,(e.stateNode.isHidden=T)&&!Y&&e.mode&1)for(ne=e,Y=e.child;Y!==null;){for(q=ne=Y;ne!==null;){switch(U=ne,te=U.child,U.tag){case 0:case 11:case 14:case 15:xo(4,U,U.return);break;case 1:xr(U,U.return);var re=U.stateNode;if(typeof re.componentWillUnmount=="function"){o=U,r=U.return;try{t=o,re.props=t.memoizedProps,re.state=t.memoizedState,re.componentWillUnmount()}catch(oe){De(o,r,oe)}}break;case 5:xr(U,U.return);break;case 22:if(U.memoizedState!==null){of(q);continue}}te!==null?(te.return=U,ne=te):of(q)}Y=Y.sibling}e:for(Y=null,q=e;;){if(q.tag===5){if(Y===null){Y=q;try{a=q.stateNode,T?(u=a.style,typeof u.setProperty=="function"?u.setProperty("display","none","important"):u.display="none"):(g=q.stateNode,v=q.memoizedProps.style,p=v!=null&&v.hasOwnProperty("display")?v.display:null,g.style.display=Du("display",p))}catch(oe){De(e,e.return,oe)}}}else if(q.tag===6){if(Y===null)try{q.stateNode.nodeValue=T?"":q.memoizedProps}catch(oe){De(e,e.return,oe)}}else if((q.tag!==22&&q.tag!==23||q.memoizedState===null||q===e)&&q.child!==null){q.child.return=q,q=q.child;continue}if(q===e)break e;for(;q.sibling===null;){if(q.return===null||q.return===e)break e;Y===q&&(Y=null),q=q.return}Y===q&&(Y=null),q.sibling.return=q.return,q=q.sibling}}break;case 19:Dt(t,e),Yt(e),o&4&&ef(e);break;case 21:break;default:Dt(t,e),Yt(e)}}function Yt(e){var t=e.flags;if(t&2){try{e:{for(var r=e.return;r!==null;){if(Xd(r)){var o=r;break e}r=r.return}throw Error(s(160))}switch(o.tag){case 5:var a=o.stateNode;o.flags&32&&(br(a,""),o.flags&=-33);var u=Jd(e);da(e,u,a);break;case 3:case 4:var p=o.stateNode.containerInfo,g=Jd(e);ca(e,g,p);break;default:throw Error(s(161))}}catch(v){De(e,e.return,v)}e.flags&=-3}t&4096&&(e.flags&=-4097)}function Pg(e,t,r){ne=e,nf(e)}function nf(e,t,r){for(var o=(e.mode&1)!==0;ne!==null;){var a=ne,u=a.child;if(a.tag===22&&o){var p=a.memoizedState!==null||Ni;if(!p){var g=a.alternate,v=g!==null&&g.memoizedState!==null||ot;g=Ni;var T=ot;if(Ni=p,(ot=v)&&!T)for(ne=a;ne!==null;)p=ne,v=p.child,p.tag===22&&p.memoizedState!==null?sf(a):v!==null?(v.return=p,ne=v):sf(a);for(;u!==null;)ne=u,nf(u),u=u.sibling;ne=a,Ni=g,ot=T}rf(e)}else a.subtreeFlags&8772&&u!==null?(u.return=a,ne=u):rf(e)}}function rf(e){for(;ne!==null;){var t=ne;if(t.flags&8772){var r=t.alternate;try{if(t.flags&8772)switch(t.tag){case 0:case 11:case 15:ot||$i(5,t);break;case 1:var o=t.stateNode;if(t.flags&4&&!ot)if(r===null)o.componentDidMount();else{var a=t.elementType===t.type?r.memoizedProps:Ot(t.type,r.memoizedProps);o.componentDidUpdate(a,r.memoizedState,o.__reactInternalSnapshotBeforeUpdate)}var u=t.updateQueue;u!==null&&od(t,u,o);break;case 3:var p=t.updateQueue;if(p!==null){if(r=null,t.child!==null)switch(t.child.tag){case 5:r=t.child.stateNode;break;case 1:r=t.child.stateNode}od(t,p,r)}break;case 5:var g=t.stateNode;if(r===null&&t.flags&4){r=g;var v=t.memoizedProps;switch(t.type){case"button":case"input":case"select":case"textarea":v.autoFocus&&r.focus();break;case"img":v.src&&(r.src=v.src)}}break;case 6:break;case 4:break;case 12:break;case 13:if(t.memoizedState===null){var T=t.alternate;if(T!==null){var Y=T.memoizedState;if(Y!==null){var q=Y.dehydrated;q!==null&&Gr(q)}}}break;case 19:case 17:case 21:case 22:case 23:case 25:break;default:throw Error(s(163))}ot||t.flags&512&&ua(t)}catch(U){De(t,t.return,U)}}if(t===e){ne=null;break}if(r=t.sibling,r!==null){r.return=t.return,ne=r;break}ne=t.return}}function of(e){for(;ne!==null;){var t=ne;if(t===e){ne=null;break}var r=t.sibling;if(r!==null){r.return=t.return,ne=r;break}ne=t.return}}function sf(e){for(;ne!==null;){var t=ne;try{switch(t.tag){case 0:case 11:case 15:var r=t.return;try{$i(4,t)}catch(v){De(t,r,v)}break;case 1:var o=t.stateNode;if(typeof o.componentDidMount=="function"){var a=t.return;try{o.componentDidMount()}catch(v){De(t,a,v)}}var u=t.return;try{ua(t)}catch(v){De(t,u,v)}break;case 5:var p=t.return;try{ua(t)}catch(v){De(t,p,v)}}}catch(v){De(t,t.return,v)}if(t===e){ne=null;break}var g=t.sibling;if(g!==null){g.return=t.return,ne=g;break}ne=t.return}}var Mg=Math.ceil,Oi=X.ReactCurrentDispatcher,fa=X.ReactCurrentOwner,Pt=X.ReactCurrentBatchConfig,Ce=0,Qe=null,Ue=null,Ze=0,vt=0,vr=fn(0),Ve=0,vo=null,Dn=0,Li=0,pa=0,wo=null,ft=null,ha=0,wr=1/0,tn=null,Di=!1,ma=null,xn=null,Ii=!1,vn=null,bi=0,So=0,ga=null,zi=-1,Bi=0;function at(){return Ce&6?Ie():zi!==-1?zi:zi=Ie()}function wn(e){return e.mode&1?Ce&2&&Ze!==0?Ze&-Ze:fg.transition!==null?(Bi===0&&(Bi=Ju()),Bi):(e=Re,e!==0||(e=window.event,e=e===void 0?16:lc(e.type)),e):1}function It(e,t,r,o){if(50<So)throw So=0,ga=null,Error(s(185));Yr(e,r,o),(!(Ce&2)||e!==Qe)&&(e===Qe&&(!(Ce&2)&&(Li|=r),Ve===4&&Sn(e,Ze)),pt(e,o),r===1&&Ce===0&&!(t.mode&1)&&(wr=Ie()+500,hi&&hn()))}function pt(e,t){var r=e.callbackNode;fm(e,t);var o=Go(e,e===Qe?Ze:0);if(o===0)r!==null&&Gu(r),e.callbackNode=null,e.callbackPriority=0;else if(t=o&-o,e.callbackPriority!==t){if(r!=null&&Gu(r),t===1)e.tag===0?dg(af.bind(null,e)):Wc(af.bind(null,e)),lg(function(){!(Ce&6)&&hn()}),r=null;else{switch(Zu(o)){case 1:r=Gs;break;case 4:r=Ku;break;case 16:r=Vo;break;case 536870912:r=Xu;break;default:r=Vo}r=gf(r,lf.bind(null,e))}e.callbackPriority=t,e.callbackNode=r}}function lf(e,t){if(zi=-1,Bi=0,Ce&6)throw Error(s(327));var r=e.callbackNode;if(Sr()&&e.callbackNode!==r)return null;var o=Go(e,e===Qe?Ze:0);if(o===0)return null;if(o&30||o&e.expiredLanes||t)t=Fi(e,o);else{t=o;var a=Ce;Ce|=2;var u=cf();(Qe!==e||Ze!==t)&&(tn=null,wr=Ie()+500,bn(e,t));do try{Ng();break}catch(g){uf(e,g)}while(!0);Ol(),Oi.current=u,Ce=a,Ue!==null?t=0:(Qe=null,Ze=0,t=Ve)}if(t!==0){if(t===2&&(a=Ks(e),a!==0&&(o=a,t=ya(e,a))),t===1)throw r=vo,bn(e,0),Sn(e,o),pt(e,Ie()),r;if(t===6)Sn(e,o);else{if(a=e.current.alternate,!(o&30)&&!_g(a)&&(t=Fi(e,o),t===2&&(u=Ks(e),u!==0&&(o=u,t=ya(e,u))),t===1))throw r=vo,bn(e,0),Sn(e,o),pt(e,Ie()),r;switch(e.finishedWork=a,e.finishedLanes=o,t){case 0:case 1:throw Error(s(345));case 2:zn(e,ft,tn);break;case 3:if(Sn(e,o),(o&130023424)===o&&(t=ha+500-Ie(),10<t)){if(Go(e,0)!==0)break;if(a=e.suspendedLanes,(a&o)!==o){at(),e.pingedLanes|=e.suspendedLanes&a;break}e.timeoutHandle=Cl(zn.bind(null,e,ft,tn),t);break}zn(e,ft,tn);break;case 4:if(Sn(e,o),(o&4194240)===o)break;for(t=e.eventTimes,a=-1;0<o;){var p=31-Tt(o);u=1<<p,p=t[p],p>a&&(a=p),o&=~u}if(o=a,o=Ie()-o,o=(120>o?120:480>o?480:1080>o?1080:1920>o?1920:3e3>o?3e3:4320>o?4320:1960*Mg(o/1960))-o,10<o){e.timeoutHandle=Cl(zn.bind(null,e,ft,tn),o);break}zn(e,ft,tn);break;case 5:zn(e,ft,tn);break;default:throw Error(s(329))}}}return pt(e,Ie()),e.callbackNode===r?lf.bind(null,e):null}function ya(e,t){var r=wo;return e.current.memoizedState.isDehydrated&&(bn(e,t).flags|=256),e=Fi(e,t),e!==2&&(t=ft,ft=r,t!==null&&xa(t)),e}function xa(e){ft===null?ft=e:ft.push.apply(ft,e)}function _g(e){for(var t=e;;){if(t.flags&16384){var r=t.updateQueue;if(r!==null&&(r=r.stores,r!==null))for(var o=0;o<r.length;o++){var a=r[o],u=a.getSnapshot;a=a.value;try{if(!Nt(u(),a))return!1}catch{return!1}}}if(r=t.child,t.subtreeFlags&16384&&r!==null)r.return=t,t=r;else{if(t===e)break;for(;t.sibling===null;){if(t.return===null||t.return===e)return!0;t=t.return}t.sibling.return=t.return,t=t.sibling}}return!0}function Sn(e,t){for(t&=~pa,t&=~Li,e.suspendedLanes|=t,e.pingedLanes&=~t,e=e.expirationTimes;0<t;){var r=31-Tt(t),o=1<<r;e[r]=-1,t&=~o}}function af(e){if(Ce&6)throw Error(s(327));Sr();var t=Go(e,0);if(!(t&1))return pt(e,Ie()),null;var r=Fi(e,t);if(e.tag!==0&&r===2){var o=Ks(e);o!==0&&(t=o,r=ya(e,o))}if(r===1)throw r=vo,bn(e,0),Sn(e,t),pt(e,Ie()),r;if(r===6)throw Error(s(345));return e.finishedWork=e.current.alternate,e.finishedLanes=t,zn(e,ft,tn),pt(e,Ie()),null}function va(e,t){var r=Ce;Ce|=1;try{return e(t)}finally{Ce=r,Ce===0&&(wr=Ie()+500,hi&&hn())}}function In(e){vn!==null&&vn.tag===0&&!(Ce&6)&&Sr();var t=Ce;Ce|=1;var r=Pt.transition,o=Re;try{if(Pt.transition=null,Re=1,e)return e()}finally{Re=o,Pt.transition=r,Ce=t,!(Ce&6)&&hn()}}function wa(){vt=vr.current,_e(vr)}function bn(e,t){e.finishedWork=null,e.finishedLanes=0;var r=e.timeoutHandle;if(r!==-1&&(e.timeoutHandle=-1,sg(r)),Ue!==null)for(r=Ue.return;r!==null;){var o=r;switch(Ml(o),o.tag){case 1:o=o.type.childContextTypes,o!=null&&fi();break;case 3:gr(),_e(ut),_e(tt),Ul();break;case 5:Bl(o);break;case 4:gr();break;case 13:_e($e);break;case 19:_e($e);break;case 10:Ll(o.type._context);break;case 22:case 23:wa()}r=r.return}if(Qe=e,Ue=e=kn(e.current,null),Ze=vt=t,Ve=0,vo=null,pa=Li=Dn=0,ft=wo=null,$n!==null){for(t=0;t<$n.length;t++)if(r=$n[t],o=r.interleaved,o!==null){r.interleaved=null;var a=o.next,u=r.pending;if(u!==null){var p=u.next;u.next=a,o.next=p}r.pending=o}$n=null}return e}function uf(e,t){do{var r=Ue;try{if(Ol(),Ei.current=Pi,ji){for(var o=Oe.memoizedState;o!==null;){var a=o.queue;a!==null&&(a.pending=null),o=o.next}ji=!1}if(Ln=0,qe=Ye=Oe=null,po=!1,ho=0,fa.current=null,r===null||r.return===null){Ve=1,vo=t,Ue=null;break}e:{var u=e,p=r.return,g=r,v=t;if(t=Ze,g.flags|=32768,v!==null&&typeof v=="object"&&typeof v.then=="function"){var T=v,Y=g,q=Y.tag;if(!(Y.mode&1)&&(q===0||q===11||q===15)){var U=Y.alternate;U?(Y.updateQueue=U.updateQueue,Y.memoizedState=U.memoizedState,Y.lanes=U.lanes):(Y.updateQueue=null,Y.memoizedState=null)}var te=$d(p);if(te!==null){te.flags&=-257,Od(te,p,g,u,t),te.mode&1&&Nd(u,T,t),t=te,v=T;var re=t.updateQueue;if(re===null){var oe=new Set;oe.add(v),t.updateQueue=oe}else re.add(v);break e}else{if(!(t&1)){Nd(u,T,t),Sa();break e}v=Error(s(426))}}else if(Ne&&g.mode&1){var be=$d(p);if(be!==null){!(be.flags&65536)&&(be.flags|=256),Od(be,p,g,u,t),Nl(yr(v,g));break e}}u=v=yr(v,g),Ve!==4&&(Ve=2),wo===null?wo=[u]:wo.push(u),u=p;do{switch(u.tag){case 3:u.flags|=65536,t&=-t,u.lanes|=t;var R=_d(u,v,t);rd(u,R);break e;case 1:g=v;var C=u.type,M=u.stateNode;if(!(u.flags&128)&&(typeof C.getDerivedStateFromError=="function"||M!==null&&typeof M.componentDidCatch=="function"&&(xn===null||!xn.has(M)))){u.flags|=65536,t&=-t,u.lanes|=t;var K=Td(u,g,t);rd(u,K);break e}}u=u.return}while(u!==null)}ff(r)}catch(se){t=se,Ue===r&&r!==null&&(Ue=r=r.return);continue}break}while(!0)}function cf(){var e=Oi.current;return Oi.current=Pi,e===null?Pi:e}function Sa(){(Ve===0||Ve===3||Ve===2)&&(Ve=4),Qe===null||!(Dn&268435455)&&!(Li&268435455)||Sn(Qe,Ze)}function Fi(e,t){var r=Ce;Ce|=2;var o=cf();(Qe!==e||Ze!==t)&&(tn=null,bn(e,t));do try{Tg();break}catch(a){uf(e,a)}while(!0);if(Ol(),Ce=r,Oi.current=o,Ue!==null)throw Error(s(261));return Qe=null,Ze=0,Ve}function Tg(){for(;Ue!==null;)df(Ue)}function Ng(){for(;Ue!==null&&!rm();)df(Ue)}function df(e){var t=mf(e.alternate,e,vt);e.memoizedProps=e.pendingProps,t===null?ff(e):Ue=t,fa.current=null}function ff(e){var t=e;do{var r=t.alternate;if(e=t.return,t.flags&32768){if(r=jg(r,t),r!==null){r.flags&=32767,Ue=r;return}if(e!==null)e.flags|=32768,e.subtreeFlags=0,e.deletions=null;else{Ve=6,Ue=null;return}}else if(r=Eg(r,t,vt),r!==null){Ue=r;return}if(t=t.sibling,t!==null){Ue=t;return}Ue=t=e}while(t!==null);Ve===0&&(Ve=5)}function zn(e,t,r){var o=Re,a=Pt.transition;try{Pt.transition=null,Re=1,$g(e,t,r,o)}finally{Pt.transition=a,Re=o}return null}function $g(e,t,r,o){do Sr();while(vn!==null);if(Ce&6)throw Error(s(327));r=e.finishedWork;var a=e.finishedLanes;if(r===null)return null;if(e.finishedWork=null,e.finishedLanes=0,r===e.current)throw Error(s(177));e.callbackNode=null,e.callbackPriority=0;var u=r.lanes|r.childLanes;if(pm(e,u),e===Qe&&(Ue=Qe=null,Ze=0),!(r.subtreeFlags&2064)&&!(r.flags&2064)||Ii||(Ii=!0,gf(Vo,function(){return Sr(),null})),u=(r.flags&15990)!==0,r.subtreeFlags&15990||u){u=Pt.transition,Pt.transition=null;var p=Re;Re=1;var g=Ce;Ce|=4,fa.current=null,Rg(e,r),tf(r,e),Zm(Sl),Jo=!!wl,Sl=wl=null,e.current=r,Pg(r),om(),Ce=g,Re=p,Pt.transition=u}else e.current=r;if(Ii&&(Ii=!1,vn=e,bi=a),u=e.pendingLanes,u===0&&(xn=null),lm(r.stateNode),pt(e,Ie()),t!==null)for(o=e.onRecoverableError,r=0;r<t.length;r++)a=t[r],o(a.value,{componentStack:a.stack,digest:a.digest});if(Di)throw Di=!1,e=ma,ma=null,e;return bi&1&&e.tag!==0&&Sr(),u=e.pendingLanes,u&1?e===ga?So++:(So=0,ga=e):So=0,hn(),null}function Sr(){if(vn!==null){var e=Zu(bi),t=Pt.transition,r=Re;try{if(Pt.transition=null,Re=16>e?16:e,vn===null)var o=!1;else{if(e=vn,vn=null,bi=0,Ce&6)throw Error(s(331));var a=Ce;for(Ce|=4,ne=e.current;ne!==null;){var u=ne,p=u.child;if(ne.flags&16){var g=u.deletions;if(g!==null){for(var v=0;v<g.length;v++){var T=g[v];for(ne=T;ne!==null;){var Y=ne;switch(Y.tag){case 0:case 11:case 15:xo(8,Y,u)}var q=Y.child;if(q!==null)q.return=Y,ne=q;else for(;ne!==null;){Y=ne;var U=Y.sibling,te=Y.return;if(Kd(Y),Y===T){ne=null;break}if(U!==null){U.return=te,ne=U;break}ne=te}}}var re=u.alternate;if(re!==null){var oe=re.child;if(oe!==null){re.child=null;do{var be=oe.sibling;oe.sibling=null,oe=be}while(oe!==null)}}ne=u}}if(u.subtreeFlags&2064&&p!==null)p.return=u,ne=p;else e:for(;ne!==null;){if(u=ne,u.flags&2048)switch(u.tag){case 0:case 11:case 15:xo(9,u,u.return)}var R=u.sibling;if(R!==null){R.return=u.return,ne=R;break e}ne=u.return}}var C=e.current;for(ne=C;ne!==null;){p=ne;var M=p.child;if(p.subtreeFlags&2064&&M!==null)M.return=p,ne=M;else e:for(p=C;ne!==null;){if(g=ne,g.flags&2048)try{switch(g.tag){case 0:case 11:case 15:$i(9,g)}}catch(se){De(g,g.return,se)}if(g===p){ne=null;break e}var K=g.sibling;if(K!==null){K.return=g.return,ne=K;break e}ne=g.return}}if(Ce=a,hn(),Bt&&typeof Bt.onPostCommitFiberRoot=="function")try{Bt.onPostCommitFiberRoot(Wo,e)}catch{}o=!0}return o}finally{Re=r,Pt.transition=t}}return!1}function pf(e,t,r){t=yr(r,t),t=_d(e,t,1),e=gn(e,t,1),t=at(),e!==null&&(Yr(e,1,t),pt(e,t))}function De(e,t,r){if(e.tag===3)pf(e,e,r);else for(;t!==null;){if(t.tag===3){pf(t,e,r);break}else if(t.tag===1){var o=t.stateNode;if(typeof t.type.getDerivedStateFromError=="function"||typeof o.componentDidCatch=="function"&&(xn===null||!xn.has(o))){e=yr(r,e),e=Td(t,e,1),t=gn(t,e,1),e=at(),t!==null&&(Yr(t,1,e),pt(t,e));break}}t=t.return}}function Og(e,t,r){var o=e.pingCache;o!==null&&o.delete(t),t=at(),e.pingedLanes|=e.suspendedLanes&r,Qe===e&&(Ze&r)===r&&(Ve===4||Ve===3&&(Ze&130023424)===Ze&&500>Ie()-ha?bn(e,0):pa|=r),pt(e,t)}function hf(e,t){t===0&&(e.mode&1?(t=Qo,Qo<<=1,!(Qo&130023424)&&(Qo=4194304)):t=1);var r=at();e=Jt(e,t),e!==null&&(Yr(e,t,r),pt(e,r))}function Lg(e){var t=e.memoizedState,r=0;t!==null&&(r=t.retryLane),hf(e,r)}function Dg(e,t){var r=0;switch(e.tag){case 13:var o=e.stateNode,a=e.memoizedState;a!==null&&(r=a.retryLane);break;case 19:o=e.stateNode;break;default:throw Error(s(314))}o!==null&&o.delete(t),hf(e,r)}var mf;mf=function(e,t,r){if(e!==null)if(e.memoizedProps!==t.pendingProps||ut.current)dt=!0;else{if(!(e.lanes&r)&&!(t.flags&128))return dt=!1,Cg(e,t,r);dt=!!(e.flags&131072)}else dt=!1,Ne&&t.flags&1048576&&qc(t,gi,t.index);switch(t.lanes=0,t.tag){case 2:var o=t.type;Ti(e,t),e=t.pendingProps;var a=ur(t,tt.current);mr(t,r),a=Vl(null,t,o,e,a,r);var u=Wl();return t.flags|=1,typeof a=="object"&&a!==null&&typeof a.render=="function"&&a.$$typeof===void 0?(t.tag=1,t.memoizedState=null,t.updateQueue=null,ct(o)?(u=!0,pi(t)):u=!1,t.memoizedState=a.state!==null&&a.state!==void 0?a.state:null,bl(t),a.updater=Mi,t.stateNode=a,a._reactInternals=t,Jl(t,o,e,r),t=na(null,t,o,!0,u,r)):(t.tag=0,Ne&&u&&Pl(t),lt(null,t,a,r),t=t.child),t;case 16:o=t.elementType;e:{switch(Ti(e,t),e=t.pendingProps,a=o._init,o=a(o._payload),t.type=o,a=t.tag=bg(o),e=Ot(o,e),a){case 0:t=ta(null,t,o,e,r);break e;case 1:t=Bd(null,t,o,e,r);break e;case 11:t=Ld(null,t,o,e,r);break e;case 14:t=Dd(null,t,o,Ot(o.type,e),r);break e}throw Error(s(306,o,""))}return t;case 0:return o=t.type,a=t.pendingProps,a=t.elementType===o?a:Ot(o,a),ta(e,t,o,a,r);case 1:return o=t.type,a=t.pendingProps,a=t.elementType===o?a:Ot(o,a),Bd(e,t,o,a,r);case 3:e:{if(Fd(t),e===null)throw Error(s(387));o=t.pendingProps,u=t.memoizedState,a=u.element,nd(e,t),ki(t,o,null,r);var p=t.memoizedState;if(o=p.element,u.isDehydrated)if(u={element:o,isDehydrated:!1,cache:p.cache,pendingSuspenseBoundaries:p.pendingSuspenseBoundaries,transitions:p.transitions},t.updateQueue.baseState=u,t.memoizedState=u,t.flags&256){a=yr(Error(s(423)),t),t=Ud(e,t,o,r,a);break e}else if(o!==a){a=yr(Error(s(424)),t),t=Ud(e,t,o,r,a);break e}else for(xt=dn(t.stateNode.containerInfo.firstChild),yt=t,Ne=!0,$t=null,r=ed(t,null,o,r),t.child=r;r;)r.flags=r.flags&-3|4096,r=r.sibling;else{if(fr(),o===a){t=en(e,t,r);break e}lt(e,t,o,r)}t=t.child}return t;case 5:return id(t),e===null&&Tl(t),o=t.type,a=t.pendingProps,u=e!==null?e.memoizedProps:null,p=a.children,kl(o,a)?p=null:u!==null&&kl(o,u)&&(t.flags|=32),zd(e,t),lt(e,t,p,r),t.child;case 6:return e===null&&Tl(t),null;case 13:return Hd(e,t,r);case 4:return zl(t,t.stateNode.containerInfo),o=t.pendingProps,e===null?t.child=pr(t,null,o,r):lt(e,t,o,r),t.child;case 11:return o=t.type,a=t.pendingProps,a=t.elementType===o?a:Ot(o,a),Ld(e,t,o,a,r);case 7:return lt(e,t,t.pendingProps,r),t.child;case 8:return lt(e,t,t.pendingProps.children,r),t.child;case 12:return lt(e,t,t.pendingProps.children,r),t.child;case 10:e:{if(o=t.type._context,a=t.pendingProps,u=t.memoizedProps,p=a.value,Pe(vi,o._currentValue),o._currentValue=p,u!==null)if(Nt(u.value,p)){if(u.children===a.children&&!ut.current){t=en(e,t,r);break e}}else for(u=t.child,u!==null&&(u.return=t);u!==null;){var g=u.dependencies;if(g!==null){p=u.child;for(var v=g.firstContext;v!==null;){if(v.context===o){if(u.tag===1){v=Zt(-1,r&-r),v.tag=2;var T=u.updateQueue;if(T!==null){T=T.shared;var Y=T.pending;Y===null?v.next=v:(v.next=Y.next,Y.next=v),T.pending=v}}u.lanes|=r,v=u.alternate,v!==null&&(v.lanes|=r),Dl(u.return,r,t),g.lanes|=r;break}v=v.next}}else if(u.tag===10)p=u.type===t.type?null:u.child;else if(u.tag===18){if(p=u.return,p===null)throw Error(s(341));p.lanes|=r,g=p.alternate,g!==null&&(g.lanes|=r),Dl(p,r,t),p=u.sibling}else p=u.child;if(p!==null)p.return=u;else for(p=u;p!==null;){if(p===t){p=null;break}if(u=p.sibling,u!==null){u.return=p.return,p=u;break}p=p.return}u=p}lt(e,t,a.children,r),t=t.child}return t;case 9:return a=t.type,o=t.pendingProps.children,mr(t,r),a=At(a),o=o(a),t.flags|=1,lt(e,t,o,r),t.child;case 14:return o=t.type,a=Ot(o,t.pendingProps),a=Ot(o.type,a),Dd(e,t,o,a,r);case 15:return Id(e,t,t.type,t.pendingProps,r);case 17:return o=t.type,a=t.pendingProps,a=t.elementType===o?a:Ot(o,a),Ti(e,t),t.tag=1,ct(o)?(e=!0,pi(t)):e=!1,mr(t,r),Pd(t,o,a),Jl(t,o,a,r),na(null,t,o,!0,e,r);case 19:return Vd(e,t,r);case 22:return bd(e,t,r)}throw Error(s(156,t.tag))};function gf(e,t){return Qu(e,t)}function Ig(e,t,r,o){this.tag=e,this.key=r,this.sibling=this.child=this.return=this.stateNode=this.type=this.elementType=null,this.index=0,this.ref=null,this.pendingProps=t,this.dependencies=this.memoizedState=this.updateQueue=this.memoizedProps=null,this.mode=o,this.subtreeFlags=this.flags=0,this.deletions=null,this.childLanes=this.lanes=0,this.alternate=null}function Mt(e,t,r,o){return new Ig(e,t,r,o)}function ka(e){return e=e.prototype,!(!e||!e.isReactComponent)}function bg(e){if(typeof e=="function")return ka(e)?1:0;if(e!=null){if(e=e.$$typeof,e===Be)return 11;if(e===z)return 14}return 2}function kn(e,t){var r=e.alternate;return r===null?(r=Mt(e.tag,t,e.key,e.mode),r.elementType=e.elementType,r.type=e.type,r.stateNode=e.stateNode,r.alternate=e,e.alternate=r):(r.pendingProps=t,r.type=e.type,r.flags=0,r.subtreeFlags=0,r.deletions=null),r.flags=e.flags&14680064,r.childLanes=e.childLanes,r.lanes=e.lanes,r.child=e.child,r.memoizedProps=e.memoizedProps,r.memoizedState=e.memoizedState,r.updateQueue=e.updateQueue,t=e.dependencies,r.dependencies=t===null?null:{lanes:t.lanes,firstContext:t.firstContext},r.sibling=e.sibling,r.index=e.index,r.ref=e.ref,r}function Ui(e,t,r,o,a,u){var p=2;if(o=e,typeof e=="function")ka(e)&&(p=1);else if(typeof e=="string")p=5;else e:switch(e){case Q:return Bn(r.children,a,u,t);case le:p=8,a|=8;break;case Se:return e=Mt(12,r,t,a|2),e.elementType=Se,e.lanes=u,e;case Fe:return e=Mt(13,r,t,a),e.elementType=Fe,e.lanes=u,e;case V:return e=Mt(19,r,t,a),e.elementType=V,e.lanes=u,e;case W:return Hi(r,a,u,t);default:if(typeof e=="object"&&e!==null)switch(e.$$typeof){case ge:p=10;break e;case pe:p=9;break e;case Be:p=11;break e;case z:p=14;break e;case b:p=16,o=null;break e}throw Error(s(130,e==null?e:typeof e,""))}return t=Mt(p,r,t,a),t.elementType=e,t.type=o,t.lanes=u,t}function Bn(e,t,r,o){return e=Mt(7,e,o,t),e.lanes=r,e}function Hi(e,t,r,o){return e=Mt(22,e,o,t),e.elementType=W,e.lanes=r,e.stateNode={isHidden:!1},e}function Ca(e,t,r){return e=Mt(6,e,null,t),e.lanes=r,e}function Ea(e,t,r){return t=Mt(4,e.children!==null?e.children:[],e.key,t),t.lanes=r,t.stateNode={containerInfo:e.containerInfo,pendingChildren:null,implementation:e.implementation},t}function zg(e,t,r,o,a){this.tag=t,this.containerInfo=e,this.finishedWork=this.pingCache=this.current=this.pendingChildren=null,this.timeoutHandle=-1,this.callbackNode=this.pendingContext=this.context=null,this.callbackPriority=0,this.eventTimes=Xs(0),this.expirationTimes=Xs(-1),this.entangledLanes=this.finishedLanes=this.mutableReadLanes=this.expiredLanes=this.pingedLanes=this.suspendedLanes=this.pendingLanes=0,this.entanglements=Xs(0),this.identifierPrefix=o,this.onRecoverableError=a,this.mutableSourceEagerHydrationData=null}function ja(e,t,r,o,a,u,p,g,v){return e=new zg(e,t,r,g,v),t===1?(t=1,u===!0&&(t|=8)):t=0,u=Mt(3,null,null,t),e.current=u,u.stateNode=e,u.memoizedState={element:o,isDehydrated:r,cache:null,transitions:null,pendingSuspenseBoundaries:null},bl(u),e}function Bg(e,t,r){var o=3<arguments.length&&arguments[3]!==void 0?arguments[3]:null;return{$$typeof:N,key:o==null?null:""+o,children:e,containerInfo:t,implementation:r}}function yf(e){if(!e)return pn;e=e._reactInternals;e:{if(Pn(e)!==e||e.tag!==1)throw Error(s(170));var t=e;do{switch(t.tag){case 3:t=t.stateNode.context;break e;case 1:if(ct(t.type)){t=t.stateNode.__reactInternalMemoizedMergedChildContext;break e}}t=t.return}while(t!==null);throw Error(s(171))}if(e.tag===1){var r=e.type;if(ct(r))return Yc(e,r,t)}return t}function xf(e,t,r,o,a,u,p,g,v){return e=ja(r,o,!0,e,a,u,p,g,v),e.context=yf(null),r=e.current,o=at(),a=wn(r),u=Zt(o,a),u.callback=t??null,gn(r,u,a),e.current.lanes=a,Yr(e,a,o),pt(e,o),e}function Yi(e,t,r,o){var a=t.current,u=at(),p=wn(a);return r=yf(r),t.context===null?t.context=r:t.pendingContext=r,t=Zt(u,p),t.payload={element:e},o=o===void 0?null:o,o!==null&&(t.callback=o),e=gn(a,t,p),e!==null&&(It(e,a,p,u),Si(e,a,p)),p}function Vi(e){if(e=e.current,!e.child)return null;switch(e.child.tag){case 5:return e.child.stateNode;default:return e.child.stateNode}}function vf(e,t){if(e=e.memoizedState,e!==null&&e.dehydrated!==null){var r=e.retryLane;e.retryLane=r!==0&&r<t?r:t}}function Aa(e,t){vf(e,t),(e=e.alternate)&&vf(e,t)}var wf=typeof reportError=="function"?reportError:function(e){console.error(e)};function Ra(e){this._internalRoot=e}Wi.prototype.render=Ra.prototype.render=function(e){var t=this._internalRoot;if(t===null)throw Error(s(409));Yi(e,t,null,null)},Wi.prototype.unmount=Ra.prototype.unmount=function(){var e=this._internalRoot;if(e!==null){this._internalRoot=null;var t=e.containerInfo;In(function(){Yi(null,e,null,null)}),t[Qt]=null}};function Wi(e){this._internalRoot=e}Wi.prototype.unstable_scheduleHydration=function(e){if(e){var t=nc();e={blockedOn:null,target:e,priority:t};for(var r=0;r<an.length&&t!==0&&t<an[r].priority;r++);an.splice(r,0,e),r===0&&ic(e)}};function Pa(e){return!(!e||e.nodeType!==1&&e.nodeType!==9&&e.nodeType!==11)}function qi(e){return!(!e||e.nodeType!==1&&e.nodeType!==9&&e.nodeType!==11&&(e.nodeType!==8||e.nodeValue!==" react-mount-point-unstable "))}function Sf(){}function Fg(e,t,r,o,a){if(a){if(typeof o=="function"){var u=o;o=function(){var T=Vi(p);u.call(T)}}var p=xf(t,o,e,0,null,!1,!1,"",Sf);return e._reactRootContainer=p,e[Qt]=p.current,oo(e.nodeType===8?e.parentNode:e),In(),p}for(;a=e.lastChild;)e.removeChild(a);if(typeof o=="function"){var g=o;o=function(){var T=Vi(v);g.call(T)}}var v=ja(e,0,!1,null,null,!1,!1,"",Sf);return e._reactRootContainer=v,e[Qt]=v.current,oo(e.nodeType===8?e.parentNode:e),In(function(){Yi(t,v,r,o)}),v}function Qi(e,t,r,o,a){var u=r._reactRootContainer;if(u){var p=u;if(typeof a=="function"){var g=a;a=function(){var v=Vi(p);g.call(v)}}Yi(t,p,e,a)}else p=Fg(r,t,e,a,o);return Vi(p)}ec=function(e){switch(e.tag){case 3:var t=e.stateNode;if(t.current.memoizedState.isDehydrated){var r=Hr(t.pendingLanes);r!==0&&(Js(t,r|1),pt(t,Ie()),!(Ce&6)&&(wr=Ie()+500,hn()))}break;case 13:In(function(){var o=Jt(e,1);if(o!==null){var a=at();It(o,e,1,a)}}),Aa(e,1)}},Zs=function(e){if(e.tag===13){var t=Jt(e,134217728);if(t!==null){var r=at();It(t,e,134217728,r)}Aa(e,134217728)}},tc=function(e){if(e.tag===13){var t=wn(e),r=Jt(e,t);if(r!==null){var o=at();It(r,e,t,o)}Aa(e,t)}},nc=function(){return Re},rc=function(e,t){var r=Re;try{return Re=e,t()}finally{Re=r}},Vs=function(e,t,r){switch(t){case"input":if(Is(e,r),t=r.name,r.type==="radio"&&t!=null){for(r=e;r.parentNode;)r=r.parentNode;for(r=r.querySelectorAll("input[name="+JSON.stringify(""+t)+'][type="radio"]'),t=0;t<r.length;t++){var o=r[t];if(o!==e&&o.form===e.form){var a=di(o);if(!a)throw Error(s(90));Xe(o),Is(o,a)}}}break;case"textarea":Nu(e,r);break;case"select":t=r.value,t!=null&&Xn(e,!!r.multiple,t,!1)}},Fu=va,Uu=In;var Ug={usingClientEntryPoint:!1,Events:[lo,lr,di,zu,Bu,va]},ko={findFiberByHostInstance:Mn,bundleType:0,version:"18.3.1",rendererPackageName:"react-dom"},Hg={bundleType:ko.bundleType,version:ko.version,rendererPackageName:ko.rendererPackageName,rendererConfig:ko.rendererConfig,overrideHookState:null,overrideHookStateDeletePath:null,overrideHookStateRenamePath:null,overrideProps:null,overridePropsDeletePath:null,overridePropsRenamePath:null,setErrorHandler:null,setSuspenseHandler:null,scheduleUpdate:null,currentDispatcherRef:X.ReactCurrentDispatcher,findHostInstanceByFiber:function(e){return e=Wu(e),e===null?null:e.stateNode},findFiberByHostInstance:ko.findFiberByHostInstance,findHostInstancesForRefresh:null,scheduleRefresh:null,scheduleRoot:null,setRefreshHandler:null,getCurrentFiber:null,reconcilerVersion:"18.3.1-next-f1338f8080-20240426"};if(typeof __REACT_DEVTOOLS_GLOBAL_HOOK__<"u"){var Gi=__REACT_DEVTOOLS_GLOBAL_HOOK__;if(!Gi.isDisabled&&Gi.supportsFiber)try{Wo=Gi.inject(Hg),Bt=Gi}catch{}}return ht.__SECRET_INTERNALS_DO_NOT_USE_OR_YOU_WILL_BE_FIRED=Ug,ht.createPortal=function(e,t){var r=2<arguments.length&&arguments[2]!==void 0?arguments[2]:null;if(!Pa(t))throw Error(s(200));return Bg(e,t,null,r)},ht.createRoot=function(e,t){if(!Pa(e))throw Error(s(299));var r=!1,o="",a=wf;return t!=null&&(t.unstable_strictMode===!0&&(r=!0),t.identifierPrefix!==void 0&&(o=t.identifierPrefix),t.onRecoverableError!==void 0&&(a=t.onRecoverableError)),t=ja(e,1,!1,null,null,r,!1,o,a),e[Qt]=t.current,oo(e.nodeType===8?e.parentNode:e),new Ra(t)},ht.findDOMNode=function(e){if(e==null)return null;if(e.nodeType===1)return e;var t=e._reactInternals;if(t===void 0)throw typeof e.render=="function"?Error(s(188)):(e=Object.keys(e).join(","),Error(s(268,e)));return e=Wu(t),e=e===null?null:e.stateNode,e},ht.flushSync=function(e){return In(e)},ht.hydrate=function(e,t,r){if(!qi(t))throw Error(s(200));return Qi(null,e,t,!0,r)},ht.hydrateRoot=function(e,t,r){if(!Pa(e))throw Error(s(405));var o=r!=null&&r.hydratedSources||null,a=!1,u="",p=wf;if(r!=null&&(r.unstable_strictMode===!0&&(a=!0),r.identifierPrefix!==void 0&&(u=r.identifierPrefix),r.onRecoverableError!==void 0&&(p=r.onRecoverableError)),t=xf(t,null,e,1,r??null,a,!1,u,p),e[Qt]=t.current,oo(e),o)for(e=0;e<o.length;e++)r=o[e],a=r._getVersion,a=a(r._source),t.mutableSourceEagerHydrationData==null?t.mutableSourceEagerHydrationData=[r,a]:t.mutableSourceEagerHydrationData.push(r,a);return new Wi(t)},ht.render=function(e,t,r){if(!qi(t))throw Error(s(200));return Qi(null,e,t,!1,r)},ht.unmountComponentAtNode=function(e){if(!qi(e))throw Error(s(40));return e._reactRootContainer?(In(function(){Qi(null,null,e,!1,function(){e._reactRootContainer=null,e[Qt]=null})}),!0):!1},ht.unstable_batchedUpdates=va,ht.unstable_renderSubtreeIntoContainer=function(e,t,r,o){if(!qi(r))throw Error(s(200));if(e==null||e._reactInternals===void 0)throw Error(s(38));return Qi(e,t,r,!1,o)},ht.version="18.3.1-next-f1338f8080-20240426",ht}var _f;function Zg(){if(_f)return Ta.exports;_f=1;function n(){if(!(typeof __REACT_DEVTOOLS_GLOBAL_HOOK__>"u"||typeof __REACT_DEVTOOLS_GLOBAL_HOOK__.checkDCE!="function"))try{__REACT_DEVTOOLS_GLOBAL_HOOK__.checkDCE(n)}catch(i){console.error(i)}}return n(),Ta.exports=Jg(),Ta.exports}var Tf;function ey(){if(Tf)return Ki;Tf=1;var n=Zg();return Ki.createRoot=n.createRoot,Ki.hydrateRoot=n.hydrateRoot,Ki}var ty=ey(),st=function(){return st=Object.assign||function(i){for(var s,l=1,c=arguments.length;l<c;l++){s=arguments[l];for(var d in s)Object.prototype.hasOwnProperty.call(s,d)&&(i[d]=s[d])}return i},st.apply(this,arguments)};function ys(n,i,s){if(s||arguments.length===2)for(var l=0,c=i.length,d;l<c;l++)(d||!(l in i))&&(d||(d=Array.prototype.slice.call(i,0,l)),d[l]=i[l]);return n.concat(d||Array.prototype.slice.call(i))}var Te="-ms-",_o="-moz-",Ae="-webkit-",Lp="comm",Es="rule",yu="decl",ny="@import",Dp="@keyframes",ry="@layer",Ip=Math.abs,xu=String.fromCharCode,Za=Object.assign;function oy(n,i){return Ke(n,0)^45?(((i<<2^Ke(n,0))<<2^Ke(n,1))<<2^Ke(n,2))<<2^Ke(n,3):0}function bp(n){return n.trim()}function nn(n,i){return(n=i.exec(n))?n[0]:n}function ve(n,i,s){return n.replace(i,s)}function ss(n,i,s){return n.indexOf(i,s)}function Ke(n,i){return n.charCodeAt(i)|0}function Pr(n,i,s){return n.slice(i,s)}function Wt(n){return n.length}function zp(n){return n.length}function Mo(n,i){return i.push(n),n}function iy(n,i){return n.map(i).join("")}function Nf(n,i){return n.filter(function(s){return!nn(s,i)})}var js=1,Mr=1,Bp=0,_t=0,He=0,$r="";function As(n,i,s,l,c,d,f,m){return{value:n,root:i,parent:s,type:l,props:c,children:d,line:js,column:Mr,length:f,return:"",siblings:m}}function En(n,i){return Za(As("",null,null,"",null,null,0,n.siblings),n,{length:-n.length},i)}function kr(n){for(;n.root;)n=En(n.root,{children:[n]});Mo(n,n.siblings)}function sy(){return He}function ly(){return He=_t>0?Ke($r,--_t):0,Mr--,He===10&&(Mr=1,js--),He}function bt(){return He=_t<Bp?Ke($r,_t++):0,Mr++,He===10&&(Mr=1,js++),He}function Yn(){return Ke($r,_t)}function ls(){return _t}function Rs(n,i){return Pr($r,n,i)}function eu(n){switch(n){case 0:case 9:case 10:case 13:case 32:return 5;case 33:case 43:case 44:case 47:case 62:case 64:case 126:case 59:case 123:case 125:return 4;case 58:return 3;case 34:case 39:case 40:case 91:return 2;case 41:case 93:return 1}return 0}function ay(n){return js=Mr=1,Bp=Wt($r=n),_t=0,[]}function uy(n){return $r="",n}function Oa(n){return bp(Rs(_t-1,tu(n===91?n+2:n===40?n+1:n)))}function cy(n){for(;(He=Yn())&&He<33;)bt();return eu(n)>2||eu(He)>3?"":" "}function dy(n,i){for(;--i&&bt()&&!(He<48||He>102||He>57&&He<65||He>70&&He<97););return Rs(n,ls()+(i<6&&Yn()==32&&bt()==32))}function tu(n){for(;bt();)switch(He){case n:return _t;case 34:case 39:n!==34&&n!==39&&tu(He);break;case 40:n===41&&tu(n);break;case 92:bt();break}return _t}function fy(n,i){for(;bt()&&n+He!==57;)if(n+He===84&&Yn()===47)break;return"/*"+Rs(i,_t-1)+"*"+xu(n===47?n:bt())}function py(n){for(;!eu(Yn());)bt();return Rs(n,_t)}function hy(n){return uy(as("",null,null,null,[""],n=ay(n),0,[0],n))}function as(n,i,s,l,c,d,f,m,x){for(var y=0,S=0,j=f,$=0,I=0,k=0,A=1,_=1,J=1,G=0,H="",X=c,D=d,N=l,Q=H;_;)switch(k=G,G=bt()){case 40:if(k!=108&&Ke(Q,j-1)==58){ss(Q+=ve(Oa(G),"&","&\f"),"&\f",Ip(y?m[y-1]:0))!=-1&&(J=-1);break}case 34:case 39:case 91:Q+=Oa(G);break;case 9:case 10:case 13:case 32:Q+=cy(k);break;case 92:Q+=dy(ls()-1,7);continue;case 47:switch(Yn()){case 42:case 47:Mo(my(fy(bt(),ls()),i,s,x),x);break;default:Q+="/"}break;case 123*A:m[y++]=Wt(Q)*J;case 125*A:case 59:case 0:switch(G){case 0:case 125:_=0;case 59+S:J==-1&&(Q=ve(Q,/\f/g,"")),I>0&&Wt(Q)-j&&Mo(I>32?Of(Q+";",l,s,j-1,x):Of(ve(Q," ","")+";",l,s,j-2,x),x);break;case 59:Q+=";";default:if(Mo(N=$f(Q,i,s,y,S,c,m,H,X=[],D=[],j,d),d),G===123)if(S===0)as(Q,i,N,N,X,d,j,m,D);else switch($===99&&Ke(Q,3)===110?100:$){case 100:case 108:case 109:case 115:as(n,N,N,l&&Mo($f(n,N,N,0,0,c,m,H,c,X=[],j,D),D),c,D,j,m,l?X:D);break;default:as(Q,N,N,N,[""],D,0,m,D)}}y=S=I=0,A=J=1,H=Q="",j=f;break;case 58:j=1+Wt(Q),I=k;default:if(A<1){if(G==123)--A;else if(G==125&&A++==0&&ly()==125)continue}switch(Q+=xu(G),G*A){case 38:J=S>0?1:(Q+="\f",-1);break;case 44:m[y++]=(Wt(Q)-1)*J,J=1;break;case 64:Yn()===45&&(Q+=Oa(bt())),$=Yn(),S=j=Wt(H=Q+=py(ls())),G++;break;case 45:k===45&&Wt(Q)==2&&(A=0)}}return d}function $f(n,i,s,l,c,d,f,m,x,y,S,j){for(var $=c-1,I=c===0?d:[""],k=zp(I),A=0,_=0,J=0;A<l;++A)for(var G=0,H=Pr(n,$+1,$=Ip(_=f[A])),X=n;G<k;++G)(X=bp(_>0?I[G]+" "+H:ve(H,/&\f/g,I[G])))&&(x[J++]=X);return As(n,i,s,c===0?Es:m,x,y,S,j)}function my(n,i,s,l){return As(n,i,s,Lp,xu(sy()),Pr(n,2,-2),0,l)}function Of(n,i,s,l,c){return As(n,i,s,yu,Pr(n,0,l),Pr(n,l+1,-1),l,c)}function Fp(n,i,s){switch(oy(n,i)){case 5103:return Ae+"print-"+n+n;case 5737:case 4201:case 3177:case 3433:case 1641:case 4457:case 2921:case 5572:case 6356:case 5844:case 3191:case 6645:case 3005:case 6391:case 5879:case 5623:case 6135:case 4599:case 4855:case 4215:case 6389:case 5109:case 5365:case 5621:case 3829:return Ae+n+n;case 4789:return _o+n+n;case 5349:case 4246:case 4810:case 6968:case 2756:return Ae+n+_o+n+Te+n+n;case 5936:switch(Ke(n,i+11)){case 114:return Ae+n+Te+ve(n,/[svh]\w+-[tblr]{2}/,"tb")+n;case 108:return Ae+n+Te+ve(n,/[svh]\w+-[tblr]{2}/,"tb-rl")+n;case 45:return Ae+n+Te+ve(n,/[svh]\w+-[tblr]{2}/,"lr")+n}case 6828:case 4268:case 2903:return Ae+n+Te+n+n;case 6165:return Ae+n+Te+"flex-"+n+n;case 5187:return Ae+n+ve(n,/(\w+).+(:[^]+)/,Ae+"box-$1$2"+Te+"flex-$1$2")+n;case 5443:return Ae+n+Te+"flex-item-"+ve(n,/flex-|-self/g,"")+(nn(n,/flex-|baseline/)?"":Te+"grid-row-"+ve(n,/flex-|-self/g,""))+n;case 4675:return Ae+n+Te+"flex-line-pack"+ve(n,/align-content|flex-|-self/g,"")+n;case 5548:return Ae+n+Te+ve(n,"shrink","negative")+n;case 5292:return Ae+n+Te+ve(n,"basis","preferred-size")+n;case 6060:return Ae+"box-"+ve(n,"-grow","")+Ae+n+Te+ve(n,"grow","positive")+n;case 4554:return Ae+ve(n,/([^-])(transform)/g,"$1"+Ae+"$2")+n;case 6187:return ve(ve(ve(n,/(zoom-|grab)/,Ae+"$1"),/(image-set)/,Ae+"$1"),n,"")+n;case 5495:case 3959:return ve(n,/(image-set\([^]*)/,Ae+"$1$`$1");case 4968:return ve(ve(n,/(.+:)(flex-)?(.*)/,Ae+"box-pack:$3"+Te+"flex-pack:$3"),/s.+-b[^;]+/,"justify")+Ae+n+n;case 4200:if(!nn(n,/flex-|baseline/))return Te+"grid-column-align"+Pr(n,i)+n;break;case 2592:case 3360:return Te+ve(n,"template-","")+n;case 4384:case 3616:return s&&s.some(function(l,c){return i=c,nn(l.props,/grid-\w+-end/)})?~ss(n+(s=s[i].value),"span",0)?n:Te+ve(n,"-start","")+n+Te+"grid-row-span:"+(~ss(s,"span",0)?nn(s,/\d+/):+nn(s,/\d+/)-+nn(n,/\d+/))+";":Te+ve(n,"-start","")+n;case 4896:case 4128:return s&&s.some(function(l){return nn(l.props,/grid-\w+-start/)})?n:Te+ve(ve(n,"-end","-span"),"span ","")+n;case 4095:case 3583:case 4068:case 2532:return ve(n,/(.+)-inline(.+)/,Ae+"$1$2")+n;case 8116:case 7059:case 5753:case 5535:case 5445:case 5701:case 4933:case 4677:case 5533:case 5789:case 5021:case 4765:if(Wt(n)-1-i>6)switch(Ke(n,i+1)){case 109:if(Ke(n,i+4)!==45)break;case 102:return ve(n,/(.+:)(.+)-([^]+)/,"$1"+Ae+"$2-$3$1"+_o+(Ke(n,i+3)==108?"$3":"$2-$3"))+n;case 115:return~ss(n,"stretch",0)?Fp(ve(n,"stretch","fill-available"),i,s)+n:n}break;case 5152:case 5920:return ve(n,/(.+?):(\d+)(\s*\/\s*(span)?\s*(\d+))?(.*)/,function(l,c,d,f,m,x,y){return Te+c+":"+d+y+(f?Te+c+"-span:"+(m?x:+x-+d)+y:"")+n});case 4949:if(Ke(n,i+6)===121)return ve(n,":",":"+Ae)+n;break;case 6444:switch(Ke(n,Ke(n,14)===45?18:11)){case 120:return ve(n,/(.+:)([^;\s!]+)(;|(\s+)?!.+)?/,"$1"+Ae+(Ke(n,14)===45?"inline-":"")+"box$3$1"+Ae+"$2$3$1"+Te+"$2box$3")+n;case 100:return ve(n,":",":"+Te)+n}break;case 5719:case 2647:case 2135:case 3927:case 2391:return ve(n,"scroll-","scroll-snap-")+n}return n}function xs(n,i){for(var s="",l=0;l<n.length;l++)s+=i(n[l],l,n,i)||"";return s}function gy(n,i,s,l){switch(n.type){case ry:if(n.children.length)break;case ny:case yu:return n.return=n.return||n.value;case Lp:return"";case Dp:return n.return=n.value+"{"+xs(n.children,l)+"}";case Es:if(!Wt(n.value=n.props.join(",")))return""}return Wt(s=xs(n.children,l))?n.return=n.value+"{"+s+"}":""}function yy(n){var i=zp(n);return function(s,l,c,d){for(var f="",m=0;m<i;m++)f+=n[m](s,l,c,d)||"";return f}}function xy(n){return function(i){i.root||(i=i.return)&&n(i)}}function vy(n,i,s,l){if(n.length>-1&&!n.return)switch(n.type){case yu:n.return=Fp(n.value,n.length,s);return;case Dp:return xs([En(n,{value:ve(n.value,"@","@"+Ae)})],l);case Es:if(n.length)return iy(s=n.props,function(c){switch(nn(c,l=/(::plac\w+|:read-\w+)/)){case":read-only":case":read-write":kr(En(n,{props:[ve(c,/:(read-\w+)/,":"+_o+"$1")]})),kr(En(n,{props:[c]})),Za(n,{props:Nf(s,l)});break;case"::placeholder":kr(En(n,{props:[ve(c,/:(plac\w+)/,":"+Ae+"input-$1")]})),kr(En(n,{props:[ve(c,/:(plac\w+)/,":"+_o+"$1")]})),kr(En(n,{props:[ve(c,/:(plac\w+)/,Te+"input-$1")]})),kr(En(n,{props:[c]})),Za(n,{props:Nf(s,l)});break}return""})}}var wy={animationIterationCount:1,aspectRatio:1,borderImageOutset:1,borderImageSlice:1,borderImageWidth:1,boxFlex:1,boxFlexGroup:1,boxOrdinalGroup:1,columnCount:1,columns:1,flex:1,flexGrow:1,flexPositive:1,flexShrink:1,flexNegative:1,flexOrder:1,gridRow:1,gridRowEnd:1,gridRowSpan:1,gridRowStart:1,gridColumn:1,gridColumnEnd:1,gridColumnSpan:1,gridColumnStart:1,msGridRow:1,msGridRowSpan:1,msGridColumn:1,msGridColumnSpan:1,fontWeight:1,lineHeight:1,opacity:1,order:1,orphans:1,tabSize:1,widows:1,zIndex:1,zoom:1,WebkitLineClamp:1,fillOpacity:1,floodOpacity:1,stopOpacity:1,strokeDasharray:1,strokeDashoffset:1,strokeMiterlimit:1,strokeOpacity:1,strokeWidth:1},wt={},_r=typeof process<"u"&&wt!==void 0&&(wt.REACT_APP_SC_ATTR||wt.SC_ATTR)||"data-styled",Up="active",Hp="data-styled-version",Ps="6.1.14",vu=`/*!sc*/
`,vs=typeof window<"u"&&"HTMLElement"in window,Sy=!!(typeof SC_DISABLE_SPEEDY=="boolean"?SC_DISABLE_SPEEDY:typeof process<"u"&&wt!==void 0&&wt.REACT_APP_SC_DISABLE_SPEEDY!==void 0&&wt.REACT_APP_SC_DISABLE_SPEEDY!==""?wt.REACT_APP_SC_DISABLE_SPEEDY!=="false"&&wt.REACT_APP_SC_DISABLE_SPEEDY:typeof process<"u"&&wt!==void 0&&wt.SC_DISABLE_SPEEDY!==void 0&&wt.SC_DISABLE_SPEEDY!==""&&wt.SC_DISABLE_SPEEDY!=="false"&&wt.SC_DISABLE_SPEEDY),Ms=Object.freeze([]),Tr=Object.freeze({});function ky(n,i,s){return s===void 0&&(s=Tr),n.theme!==s.theme&&n.theme||i||s.theme}var Yp=new Set(["a","abbr","address","area","article","aside","audio","b","base","bdi","bdo","big","blockquote","body","br","button","canvas","caption","cite","code","col","colgroup","data","datalist","dd","del","details","dfn","dialog","div","dl","dt","em","embed","fieldset","figcaption","figure","footer","form","h1","h2","h3","h4","h5","h6","header","hgroup","hr","html","i","iframe","img","input","ins","kbd","keygen","label","legend","li","link","main","map","mark","menu","menuitem","meta","meter","nav","noscript","object","ol","optgroup","option","output","p","param","picture","pre","progress","q","rp","rt","ruby","s","samp","script","section","select","small","source","span","strong","style","sub","summary","sup","table","tbody","td","textarea","tfoot","th","thead","time","tr","track","u","ul","use","var","video","wbr","circle","clipPath","defs","ellipse","foreignObject","g","image","line","linearGradient","marker","mask","path","pattern","polygon","polyline","radialGradient","rect","stop","svg","text","tspan"]),Cy=/[!"#$%&'()*+,./:;<=>?@[\\\]^`{|}~-]+/g,Ey=/(^-|-$)/g;function Lf(n){return n.replace(Cy,"-").replace(Ey,"")}var jy=/(a)(d)/gi,Xi=52,Df=function(n){return String.fromCharCode(n+(n>25?39:97))};function nu(n){var i,s="";for(i=Math.abs(n);i>Xi;i=i/Xi|0)s=Df(i%Xi)+s;return(Df(i%Xi)+s).replace(jy,"$1-$2")}var La,Vp=5381,Er=function(n,i){for(var s=i.length;s;)n=33*n^i.charCodeAt(--s);return n},Wp=function(n){return Er(Vp,n)};function Ay(n){return nu(Wp(n)>>>0)}function Ry(n){return n.displayName||n.name||"Component"}function Da(n){return typeof n=="string"&&!0}var qp=typeof Symbol=="function"&&Symbol.for,Qp=qp?Symbol.for("react.memo"):60115,Py=qp?Symbol.for("react.forward_ref"):60112,My={childContextTypes:!0,contextType:!0,contextTypes:!0,defaultProps:!0,displayName:!0,getDefaultProps:!0,getDerivedStateFromError:!0,getDerivedStateFromProps:!0,mixins:!0,propTypes:!0,type:!0},_y={name:!0,length:!0,prototype:!0,caller:!0,callee:!0,arguments:!0,arity:!0},Gp={$$typeof:!0,compare:!0,defaultProps:!0,displayName:!0,propTypes:!0,type:!0},Ty=((La={})[Py]={$$typeof:!0,render:!0,defaultProps:!0,displayName:!0,propTypes:!0},La[Qp]=Gp,La);function If(n){return("type"in(i=n)&&i.type.$$typeof)===Qp?Gp:"$$typeof"in n?Ty[n.$$typeof]:My;var i}var Ny=Object.defineProperty,$y=Object.getOwnPropertyNames,bf=Object.getOwnPropertySymbols,Oy=Object.getOwnPropertyDescriptor,Ly=Object.getPrototypeOf,zf=Object.prototype;function Kp(n,i,s){if(typeof i!="string"){if(zf){var l=Ly(i);l&&l!==zf&&Kp(n,l,s)}var c=$y(i);bf&&(c=c.concat(bf(i)));for(var d=If(n),f=If(i),m=0;m<c.length;++m){var x=c[m];if(!(x in _y||s&&s[x]||f&&x in f||d&&x in d)){var y=Oy(i,x);try{Ny(n,x,y)}catch{}}}}return n}function qn(n){return typeof n=="function"}function wu(n){return typeof n=="object"&&"styledComponentId"in n}function Un(n,i){return n&&i?"".concat(n," ").concat(i):n||i||""}function Bf(n,i){if(n.length===0)return"";for(var s=n[0],l=1;l<n.length;l++)s+=n[l];return s}function Lo(n){return n!==null&&typeof n=="object"&&n.constructor.name===Object.name&&!("props"in n&&n.$$typeof)}function ru(n,i,s){if(s===void 0&&(s=!1),!s&&!Lo(n)&&!Array.isArray(n))return i;if(Array.isArray(i))for(var l=0;l<i.length;l++)n[l]=ru(n[l],i[l]);else if(Lo(i))for(var l in i)n[l]=ru(n[l],i[l]);return n}function Su(n,i){Object.defineProperty(n,"toString",{value:i})}function Qn(n){for(var i=[],s=1;s<arguments.length;s++)i[s-1]=arguments[s];return new Error("An error occurred. See https://github.com/styled-components/styled-components/blob/main/packages/styled-components/src/utils/errors.md#".concat(n," for more information.").concat(i.length>0?" Args: ".concat(i.join(", ")):""))}var Dy=function(){function n(i){this.groupSizes=new Uint32Array(512),this.length=512,this.tag=i}return n.prototype.indexOfGroup=function(i){for(var s=0,l=0;l<i;l++)s+=this.groupSizes[l];return s},n.prototype.insertRules=function(i,s){if(i>=this.groupSizes.length){for(var l=this.groupSizes,c=l.length,d=c;i>=d;)if((d<<=1)<0)throw Qn(16,"".concat(i));this.groupSizes=new Uint32Array(d),this.groupSizes.set(l),this.length=d;for(var f=c;f<d;f++)this.groupSizes[f]=0}for(var m=this.indexOfGroup(i+1),x=(f=0,s.length);f<x;f++)this.tag.insertRule(m,s[f])&&(this.groupSizes[i]++,m++)},n.prototype.clearGroup=function(i){if(i<this.length){var s=this.groupSizes[i],l=this.indexOfGroup(i),c=l+s;this.groupSizes[i]=0;for(var d=l;d<c;d++)this.tag.deleteRule(l)}},n.prototype.getGroup=function(i){var s="";if(i>=this.length||this.groupSizes[i]===0)return s;for(var l=this.groupSizes[i],c=this.indexOfGroup(i),d=c+l,f=c;f<d;f++)s+="".concat(this.tag.getRule(f)).concat(vu);return s},n}(),us=new Map,ws=new Map,cs=1,Ji=function(n){if(us.has(n))return us.get(n);for(;ws.has(cs);)cs++;var i=cs++;return us.set(n,i),ws.set(i,n),i},Iy=function(n,i){cs=i+1,us.set(n,i),ws.set(i,n)},by="style[".concat(_r,"][").concat(Hp,'="').concat(Ps,'"]'),zy=new RegExp("^".concat(_r,'\\.g(\\d+)\\[id="([\\w\\d-]+)"\\].*?"([^"]*)')),By=function(n,i,s){for(var l,c=s.split(","),d=0,f=c.length;d<f;d++)(l=c[d])&&n.registerName(i,l)},Fy=function(n,i){for(var s,l=((s=i.textContent)!==null&&s!==void 0?s:"").split(vu),c=[],d=0,f=l.length;d<f;d++){var m=l[d].trim();if(m){var x=m.match(zy);if(x){var y=0|parseInt(x[1],10),S=x[2];y!==0&&(Iy(S,y),By(n,S,x[3]),n.getTag().insertRules(y,c)),c.length=0}else c.push(m)}}},Ff=function(n){for(var i=document.querySelectorAll(by),s=0,l=i.length;s<l;s++){var c=i[s];c&&c.getAttribute(_r)!==Up&&(Fy(n,c),c.parentNode&&c.parentNode.removeChild(c))}};function Uy(){return typeof __webpack_nonce__<"u"?__webpack_nonce__:null}var Xp=function(n){var i=document.head,s=n||i,l=document.createElement("style"),c=function(m){var x=Array.from(m.querySelectorAll("style[".concat(_r,"]")));return x[x.length-1]}(s),d=c!==void 0?c.nextSibling:null;l.setAttribute(_r,Up),l.setAttribute(Hp,Ps);var f=Uy();return f&&l.setAttribute("nonce",f),s.insertBefore(l,d),l},Hy=function(){function n(i){this.element=Xp(i),this.element.appendChild(document.createTextNode("")),this.sheet=function(s){if(s.sheet)return s.sheet;for(var l=document.styleSheets,c=0,d=l.length;c<d;c++){var f=l[c];if(f.ownerNode===s)return f}throw Qn(17)}(this.element),this.length=0}return n.prototype.insertRule=function(i,s){try{return this.sheet.insertRule(s,i),this.length++,!0}catch{return!1}},n.prototype.deleteRule=function(i){this.sheet.deleteRule(i),this.length--},n.prototype.getRule=function(i){var s=this.sheet.cssRules[i];return s&&s.cssText?s.cssText:""},n}(),Yy=function(){function n(i){this.element=Xp(i),this.nodes=this.element.childNodes,this.length=0}return n.prototype.insertRule=function(i,s){if(i<=this.length&&i>=0){var l=document.createTextNode(s);return this.element.insertBefore(l,this.nodes[i]||null),this.length++,!0}return!1},n.prototype.deleteRule=function(i){this.element.removeChild(this.nodes[i]),this.length--},n.prototype.getRule=function(i){return i<this.length?this.nodes[i].textContent:""},n}(),Vy=function(){function n(i){this.rules=[],this.length=0}return n.prototype.insertRule=function(i,s){return i<=this.length&&(this.rules.splice(i,0,s),this.length++,!0)},n.prototype.deleteRule=function(i){this.rules.splice(i,1),this.length--},n.prototype.getRule=function(i){return i<this.length?this.rules[i]:""},n}(),Uf=vs,Wy={isServer:!vs,useCSSOMInjection:!Sy},Jp=function(){function n(i,s,l){i===void 0&&(i=Tr),s===void 0&&(s={});var c=this;this.options=st(st({},Wy),i),this.gs=s,this.names=new Map(l),this.server=!!i.isServer,!this.server&&vs&&Uf&&(Uf=!1,Ff(this)),Su(this,function(){return function(d){for(var f=d.getTag(),m=f.length,x="",y=function(j){var $=function(J){return ws.get(J)}(j);if($===void 0)return"continue";var I=d.names.get($),k=f.getGroup(j);if(I===void 0||!I.size||k.length===0)return"continue";var A="".concat(_r,".g").concat(j,'[id="').concat($,'"]'),_="";I!==void 0&&I.forEach(function(J){J.length>0&&(_+="".concat(J,","))}),x+="".concat(k).concat(A,'{content:"').concat(_,'"}').concat(vu)},S=0;S<m;S++)y(S);return x}(c)})}return n.registerId=function(i){return Ji(i)},n.prototype.rehydrate=function(){!this.server&&vs&&Ff(this)},n.prototype.reconstructWithOptions=function(i,s){return s===void 0&&(s=!0),new n(st(st({},this.options),i),this.gs,s&&this.names||void 0)},n.prototype.allocateGSInstance=function(i){return this.gs[i]=(this.gs[i]||0)+1},n.prototype.getTag=function(){return this.tag||(this.tag=(i=function(s){var l=s.useCSSOMInjection,c=s.target;return s.isServer?new Vy(c):l?new Hy(c):new Yy(c)}(this.options),new Dy(i)));var i},n.prototype.hasNameForId=function(i,s){return this.names.has(i)&&this.names.get(i).has(s)},n.prototype.registerName=function(i,s){if(Ji(i),this.names.has(i))this.names.get(i).add(s);else{var l=new Set;l.add(s),this.names.set(i,l)}},n.prototype.insertRules=function(i,s,l){this.registerName(i,s),this.getTag().insertRules(Ji(i),l)},n.prototype.clearNames=function(i){this.names.has(i)&&this.names.get(i).clear()},n.prototype.clearRules=function(i){this.getTag().clearGroup(Ji(i)),this.clearNames(i)},n.prototype.clearTag=function(){this.tag=void 0},n}(),qy=/&/g,Qy=/^\s*\/\/.*$/gm;function Zp(n,i){return n.map(function(s){return s.type==="rule"&&(s.value="".concat(i," ").concat(s.value),s.value=s.value.replaceAll(",",",".concat(i," ")),s.props=s.props.map(function(l){return"".concat(i," ").concat(l)})),Array.isArray(s.children)&&s.type!=="@keyframes"&&(s.children=Zp(s.children,i)),s})}function Gy(n){var i,s,l,c=Tr,d=c.options,f=d===void 0?Tr:d,m=c.plugins,x=m===void 0?Ms:m,y=function($,I,k){return k.startsWith(s)&&k.endsWith(s)&&k.replaceAll(s,"").length>0?".".concat(i):$},S=x.slice();S.push(function($){$.type===Es&&$.value.includes("&")&&($.props[0]=$.props[0].replace(qy,s).replace(l,y))}),f.prefix&&S.push(vy),S.push(gy);var j=function($,I,k,A){I===void 0&&(I=""),k===void 0&&(k=""),A===void 0&&(A="&"),i=A,s=I,l=new RegExp("\\".concat(s,"\\b"),"g");var _=$.replace(Qy,""),J=hy(k||I?"".concat(k," ").concat(I," { ").concat(_," }"):_);f.namespace&&(J=Zp(J,f.namespace));var G=[];return xs(J,yy(S.concat(xy(function(H){return G.push(H)})))),G};return j.hash=x.length?x.reduce(function($,I){return I.name||Qn(15),Er($,I.name)},Vp).toString():"",j}var Ky=new Jp,ou=Gy(),eh=St.createContext({shouldForwardProp:void 0,styleSheet:Ky,stylis:ou});eh.Consumer;St.createContext(void 0);function Hf(){return Z.useContext(eh)}var Xy=function(){function n(i,s){var l=this;this.inject=function(c,d){d===void 0&&(d=ou);var f=l.name+d.hash;c.hasNameForId(l.id,f)||c.insertRules(l.id,f,d(l.rules,f,"@keyframes"))},this.name=i,this.id="sc-keyframes-".concat(i),this.rules=s,Su(this,function(){throw Qn(12,String(l.name))})}return n.prototype.getName=function(i){return i===void 0&&(i=ou),this.name+i.hash},n}(),Jy=function(n){return n>="A"&&n<="Z"};function Yf(n){for(var i="",s=0;s<n.length;s++){var l=n[s];if(s===1&&l==="-"&&n[0]==="-")return n;Jy(l)?i+="-"+l.toLowerCase():i+=l}return i.startsWith("ms-")?"-"+i:i}var th=function(n){return n==null||n===!1||n===""},nh=function(n){var i,s,l=[];for(var c in n){var d=n[c];n.hasOwnProperty(c)&&!th(d)&&(Array.isArray(d)&&d.isCss||qn(d)?l.push("".concat(Yf(c),":"),d,";"):Lo(d)?l.push.apply(l,ys(ys(["".concat(c," {")],nh(d),!1),["}"],!1)):l.push("".concat(Yf(c),": ").concat((i=c,(s=d)==null||typeof s=="boolean"||s===""?"":typeof s!="number"||s===0||i in wy||i.startsWith("--")?String(s).trim():"".concat(s,"px")),";")))}return l};function Vn(n,i,s,l){if(th(n))return[];if(wu(n))return[".".concat(n.styledComponentId)];if(qn(n)){if(!qn(d=n)||d.prototype&&d.prototype.isReactComponent||!i)return[n];var c=n(i);return Vn(c,i,s,l)}var d;return n instanceof Xy?s?(n.inject(s,l),[n.getName(l)]):[n]:Lo(n)?nh(n):Array.isArray(n)?Array.prototype.concat.apply(Ms,n.map(function(f){return Vn(f,i,s,l)})):[n.toString()]}function Zy(n){for(var i=0;i<n.length;i+=1){var s=n[i];if(qn(s)&&!wu(s))return!1}return!0}var e0=Wp(Ps),t0=function(){function n(i,s,l){this.rules=i,this.staticRulesId="",this.isStatic=(l===void 0||l.isStatic)&&Zy(i),this.componentId=s,this.baseHash=Er(e0,s),this.baseStyle=l,Jp.registerId(s)}return n.prototype.generateAndInjectStyles=function(i,s,l){var c=this.baseStyle?this.baseStyle.generateAndInjectStyles(i,s,l):"";if(this.isStatic&&!l.hash)if(this.staticRulesId&&s.hasNameForId(this.componentId,this.staticRulesId))c=Un(c,this.staticRulesId);else{var d=Bf(Vn(this.rules,i,s,l)),f=nu(Er(this.baseHash,d)>>>0);if(!s.hasNameForId(this.componentId,f)){var m=l(d,".".concat(f),void 0,this.componentId);s.insertRules(this.componentId,f,m)}c=Un(c,f),this.staticRulesId=f}else{for(var x=Er(this.baseHash,l.hash),y="",S=0;S<this.rules.length;S++){var j=this.rules[S];if(typeof j=="string")y+=j;else if(j){var $=Bf(Vn(j,i,s,l));x=Er(x,$+S),y+=$}}if(y){var I=nu(x>>>0);s.hasNameForId(this.componentId,I)||s.insertRules(this.componentId,I,l(y,".".concat(I),void 0,this.componentId)),c=Un(c,I)}}return c},n}(),Ss=St.createContext(void 0);Ss.Consumer;function Vf(n){var i=St.useContext(Ss),s=Z.useMemo(function(){return function(l,c){if(!l)throw Qn(14);if(qn(l)){var d=l(c);return d}if(Array.isArray(l)||typeof l!="object")throw Qn(8);return c?st(st({},c),l):l}(n.theme,i)},[n.theme,i]);return n.children?St.createElement(Ss.Provider,{value:s},n.children):null}var Ia={};function n0(n,i,s){var l=wu(n),c=n,d=!Da(n),f=i.attrs,m=f===void 0?Ms:f,x=i.componentId,y=x===void 0?function(X,D){var N=typeof X!="string"?"sc":Lf(X);Ia[N]=(Ia[N]||0)+1;var Q="".concat(N,"-").concat(Ay(Ps+N+Ia[N]));return D?"".concat(D,"-").concat(Q):Q}(i.displayName,i.parentComponentId):x,S=i.displayName,j=S===void 0?function(X){return Da(X)?"styled.".concat(X):"Styled(".concat(Ry(X),")")}(n):S,$=i.displayName&&i.componentId?"".concat(Lf(i.displayName),"-").concat(i.componentId):i.componentId||y,I=l&&c.attrs?c.attrs.concat(m).filter(Boolean):m,k=i.shouldForwardProp;if(l&&c.shouldForwardProp){var A=c.shouldForwardProp;if(i.shouldForwardProp){var _=i.shouldForwardProp;k=function(X,D){return A(X,D)&&_(X,D)}}else k=A}var J=new t0(s,$,l?c.componentStyle:void 0);function G(X,D){return function(N,Q,le){var Se=N.attrs,ge=N.componentStyle,pe=N.defaultProps,Be=N.foldedComponentIds,Fe=N.styledComponentId,V=N.target,z=St.useContext(Ss),b=Hf(),W=N.shouldForwardProp||b.shouldForwardProp,P=ky(Q,z,pe)||Tr,F=function(de,he,we){for(var ye,xe=st(st({},he),{className:void 0,theme:we}),Ee=0;Ee<de.length;Ee+=1){var We=qn(ye=de[Ee])?ye(xe):ye;for(var Xe in We)xe[Xe]=Xe==="className"?Un(xe[Xe],We[Xe]):Xe==="style"?st(st({},xe[Xe]),We[Xe]):We[Xe]}return he.className&&(xe.className=Un(xe.className,he.className)),xe}(Se,Q,P),B=F.as||V,w={};for(var L in F)F[L]===void 0||L[0]==="$"||L==="as"||L==="theme"&&F.theme===P||(L==="forwardedAs"?w.as=F.forwardedAs:W&&!W(L,B)||(w[L]=F[L]));var ie=function(de,he){var we=Hf(),ye=de.generateAndInjectStyles(he,we.styleSheet,we.stylis);return ye}(ge,F),ae=Un(Be,Fe);return ie&&(ae+=" "+ie),F.className&&(ae+=" "+F.className),w[Da(B)&&!Yp.has(B)?"class":"className"]=ae,le&&(w.ref=le),Z.createElement(B,w)}(H,X,D)}G.displayName=j;var H=St.forwardRef(G);return H.attrs=I,H.componentStyle=J,H.displayName=j,H.shouldForwardProp=k,H.foldedComponentIds=l?Un(c.foldedComponentIds,c.styledComponentId):"",H.styledComponentId=$,H.target=l?c.target:n,Object.defineProperty(H,"defaultProps",{get:function(){return this._foldedDefaultProps},set:function(X){this._foldedDefaultProps=l?function(D){for(var N=[],Q=1;Q<arguments.length;Q++)N[Q-1]=arguments[Q];for(var le=0,Se=N;le<Se.length;le++)ru(D,Se[le],!0);return D}({},c.defaultProps,X):X}}),Su(H,function(){return".".concat(H.styledComponentId)}),d&&Kp(H,n,{attrs:!0,componentStyle:!0,displayName:!0,foldedComponentIds:!0,shouldForwardProp:!0,styledComponentId:!0,target:!0}),H}function Wf(n,i){for(var s=[n[0]],l=0,c=i.length;l<c;l+=1)s.push(i[l],n[l+1]);return s}var qf=function(n){return Object.assign(n,{isCss:!0})};function r0(n){for(var i=[],s=1;s<arguments.length;s++)i[s-1]=arguments[s];if(qn(n)||Lo(n))return qf(Vn(Wf(Ms,ys([n],i,!0))));var l=n;return i.length===0&&l.length===1&&typeof l[0]=="string"?Vn(l):qf(Vn(Wf(l,i)))}function iu(n,i,s){if(s===void 0&&(s=Tr),!i)throw Qn(1,i);var l=function(c){for(var d=[],f=1;f<arguments.length;f++)d[f-1]=arguments[f];return n(i,s,r0.apply(void 0,ys([c],d,!1)))};return l.attrs=function(c){return iu(n,i,st(st({},s),{attrs:Array.prototype.concat(s.attrs,c).filter(Boolean)}))},l.withConfig=function(c){return iu(n,i,st(st({},s),c))},l}var rh=function(n){return iu(n0,n)},E=rh;Yp.forEach(function(n){E[n]=rh(n)});const Qf=n=>{let i;const s=new Set,l=(y,S)=>{const j=typeof y=="function"?y(i):y;if(!Object.is(j,i)){const $=i;i=S??(typeof j!="object"||j===null)?j:Object.assign({},i,j),s.forEach(I=>I(i,$))}},c=()=>i,m={setState:l,getState:c,getInitialState:()=>x,subscribe:y=>(s.add(y),()=>s.delete(y))},x=i=n(l,c,m);return m},o0=n=>n?Qf(n):Qf,i0=n=>n;function s0(n,i=i0){const s=St.useSyncExternalStore(n.subscribe,()=>i(n.getState()),()=>i(n.getInitialState()));return St.useDebugValue(s),s}const Gf=n=>{const i=o0(n),s=l=>s0(i,l);return Object.assign(s,i),s},Kn=n=>n?Gf(n):Gf;function oh(n,i){return function(){return n.apply(i,arguments)}}const{toString:l0}=Object.prototype,{getPrototypeOf:ku}=Object,_s=(n=>i=>{const s=l0.call(i);return n[s]||(n[s]=s.slice(8,-1).toLowerCase())})(Object.create(null)),zt=n=>(n=n.toLowerCase(),i=>_s(i)===n),Ts=n=>i=>typeof i===n,{isArray:Or}=Array,Do=Ts("undefined");function a0(n){return n!==null&&!Do(n)&&n.constructor!==null&&!Do(n.constructor)&&kt(n.constructor.isBuffer)&&n.constructor.isBuffer(n)}const ih=zt("ArrayBuffer");function u0(n){let i;return typeof ArrayBuffer<"u"&&ArrayBuffer.isView?i=ArrayBuffer.isView(n):i=n&&n.buffer&&ih(n.buffer),i}const c0=Ts("string"),kt=Ts("function"),sh=Ts("number"),Ns=n=>n!==null&&typeof n=="object",d0=n=>n===!0||n===!1,ds=n=>{if(_s(n)!=="object")return!1;const i=ku(n);return(i===null||i===Object.prototype||Object.getPrototypeOf(i)===null)&&!(Symbol.toStringTag in n)&&!(Symbol.iterator in n)},f0=zt("Date"),p0=zt("File"),h0=zt("Blob"),m0=zt("FileList"),g0=n=>Ns(n)&&kt(n.pipe),y0=n=>{let i;return n&&(typeof FormData=="function"&&n instanceof FormData||kt(n.append)&&((i=_s(n))==="formdata"||i==="object"&&kt(n.toString)&&n.toString()==="[object FormData]"))},x0=zt("URLSearchParams"),[v0,w0,S0,k0]=["ReadableStream","Request","Response","Headers"].map(zt),C0=n=>n.trim?n.trim():n.replace(/^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g,"");function bo(n,i,{allOwnKeys:s=!1}={}){if(n===null||typeof n>"u")return;let l,c;if(typeof n!="object"&&(n=[n]),Or(n))for(l=0,c=n.length;l<c;l++)i.call(null,n[l],l,n);else{const d=s?Object.getOwnPropertyNames(n):Object.keys(n),f=d.length;let m;for(l=0;l<f;l++)m=d[l],i.call(null,n[m],m,n)}}function lh(n,i){i=i.toLowerCase();const s=Object.keys(n);let l=s.length,c;for(;l-- >0;)if(c=s[l],i===c.toLowerCase())return c;return null}const Hn=typeof globalThis<"u"?globalThis:typeof self<"u"?self:typeof window<"u"?window:global,ah=n=>!Do(n)&&n!==Hn;function su(){const{caseless:n}=ah(this)&&this||{},i={},s=(l,c)=>{const d=n&&lh(i,c)||c;ds(i[d])&&ds(l)?i[d]=su(i[d],l):ds(l)?i[d]=su({},l):Or(l)?i[d]=l.slice():i[d]=l};for(let l=0,c=arguments.length;l<c;l++)arguments[l]&&bo(arguments[l],s);return i}const E0=(n,i,s,{allOwnKeys:l}={})=>(bo(i,(c,d)=>{s&&kt(c)?n[d]=oh(c,s):n[d]=c},{allOwnKeys:l}),n),j0=n=>(n.charCodeAt(0)===65279&&(n=n.slice(1)),n),A0=(n,i,s,l)=>{n.prototype=Object.create(i.prototype,l),n.prototype.constructor=n,Object.defineProperty(n,"super",{value:i.prototype}),s&&Object.assign(n.prototype,s)},R0=(n,i,s,l)=>{let c,d,f;const m={};if(i=i||{},n==null)return i;do{for(c=Object.getOwnPropertyNames(n),d=c.length;d-- >0;)f=c[d],(!l||l(f,n,i))&&!m[f]&&(i[f]=n[f],m[f]=!0);n=s!==!1&&ku(n)}while(n&&(!s||s(n,i))&&n!==Object.prototype);return i},P0=(n,i,s)=>{n=String(n),(s===void 0||s>n.length)&&(s=n.length),s-=i.length;const l=n.indexOf(i,s);return l!==-1&&l===s},M0=n=>{if(!n)return null;if(Or(n))return n;let i=n.length;if(!sh(i))return null;const s=new Array(i);for(;i-- >0;)s[i]=n[i];return s},_0=(n=>i=>n&&i instanceof n)(typeof Uint8Array<"u"&&ku(Uint8Array)),T0=(n,i)=>{const l=(n&&n[Symbol.iterator]).call(n);let c;for(;(c=l.next())&&!c.done;){const d=c.value;i.call(n,d[0],d[1])}},N0=(n,i)=>{let s;const l=[];for(;(s=n.exec(i))!==null;)l.push(s);return l},$0=zt("HTMLFormElement"),O0=n=>n.toLowerCase().replace(/[-_\s]([a-z\d])(\w*)/g,function(s,l,c){return l.toUpperCase()+c}),Kf=(({hasOwnProperty:n})=>(i,s)=>n.call(i,s))(Object.prototype),L0=zt("RegExp"),uh=(n,i)=>{const s=Object.getOwnPropertyDescriptors(n),l={};bo(s,(c,d)=>{let f;(f=i(c,d,n))!==!1&&(l[d]=f||c)}),Object.defineProperties(n,l)},D0=n=>{uh(n,(i,s)=>{if(kt(n)&&["arguments","caller","callee"].indexOf(s)!==-1)return!1;const l=n[s];if(kt(l)){if(i.enumerable=!1,"writable"in i){i.writable=!1;return}i.set||(i.set=()=>{throw Error("Can not rewrite read-only method '"+s+"'")})}})},I0=(n,i)=>{const s={},l=c=>{c.forEach(d=>{s[d]=!0})};return Or(n)?l(n):l(String(n).split(i)),s},b0=()=>{},z0=(n,i)=>n!=null&&Number.isFinite(n=+n)?n:i,ba="abcdefghijklmnopqrstuvwxyz",Xf="0123456789",ch={DIGIT:Xf,ALPHA:ba,ALPHA_DIGIT:ba+ba.toUpperCase()+Xf},B0=(n=16,i=ch.ALPHA_DIGIT)=>{let s="";const{length:l}=i;for(;n--;)s+=i[Math.random()*l|0];return s};function F0(n){return!!(n&&kt(n.append)&&n[Symbol.toStringTag]==="FormData"&&n[Symbol.iterator])}const U0=n=>{const i=new Array(10),s=(l,c)=>{if(Ns(l)){if(i.indexOf(l)>=0)return;if(!("toJSON"in l)){i[c]=l;const d=Or(l)?[]:{};return bo(l,(f,m)=>{const x=s(f,c+1);!Do(x)&&(d[m]=x)}),i[c]=void 0,d}}return l};return s(n,0)},H0=zt("AsyncFunction"),Y0=n=>n&&(Ns(n)||kt(n))&&kt(n.then)&&kt(n.catch),dh=((n,i)=>n?setImmediate:i?((s,l)=>(Hn.addEventListener("message",({source:c,data:d})=>{c===Hn&&d===s&&l.length&&l.shift()()},!1),c=>{l.push(c),Hn.postMessage(s,"*")}))(`axios@${Math.random()}`,[]):s=>setTimeout(s))(typeof setImmediate=="function",kt(Hn.postMessage)),V0=typeof queueMicrotask<"u"?queueMicrotask.bind(Hn):typeof process<"u"&&process.nextTick||dh,O={isArray:Or,isArrayBuffer:ih,isBuffer:a0,isFormData:y0,isArrayBufferView:u0,isString:c0,isNumber:sh,isBoolean:d0,isObject:Ns,isPlainObject:ds,isReadableStream:v0,isRequest:w0,isResponse:S0,isHeaders:k0,isUndefined:Do,isDate:f0,isFile:p0,isBlob:h0,isRegExp:L0,isFunction:kt,isStream:g0,isURLSearchParams:x0,isTypedArray:_0,isFileList:m0,forEach:bo,merge:su,extend:E0,trim:C0,stripBOM:j0,inherits:A0,toFlatObject:R0,kindOf:_s,kindOfTest:zt,endsWith:P0,toArray:M0,forEachEntry:T0,matchAll:N0,isHTMLForm:$0,hasOwnProperty:Kf,hasOwnProp:Kf,reduceDescriptors:uh,freezeMethods:D0,toObjectSet:I0,toCamelCase:O0,noop:b0,toFiniteNumber:z0,findKey:lh,global:Hn,isContextDefined:ah,ALPHABET:ch,generateString:B0,isSpecCompliantForm:F0,toJSONObject:U0,isAsyncFn:H0,isThenable:Y0,setImmediate:dh,asap:V0};function me(n,i,s,l,c){Error.call(this),Error.captureStackTrace?Error.captureStackTrace(this,this.constructor):this.stack=new Error().stack,this.message=n,this.name="AxiosError",i&&(this.code=i),s&&(this.config=s),l&&(this.request=l),c&&(this.response=c,this.status=c.status?c.status:null)}O.inherits(me,Error,{toJSON:function(){return{message:this.message,name:this.name,description:this.description,number:this.number,fileName:this.fileName,lineNumber:this.lineNumber,columnNumber:this.columnNumber,stack:this.stack,config:O.toJSONObject(this.config),code:this.code,status:this.status}}});const fh=me.prototype,ph={};["ERR_BAD_OPTION_VALUE","ERR_BAD_OPTION","ECONNABORTED","ETIMEDOUT","ERR_NETWORK","ERR_FR_TOO_MANY_REDIRECTS","ERR_DEPRECATED","ERR_BAD_RESPONSE","ERR_BAD_REQUEST","ERR_CANCELED","ERR_NOT_SUPPORT","ERR_INVALID_URL"].forEach(n=>{ph[n]={value:n}});Object.defineProperties(me,ph);Object.defineProperty(fh,"isAxiosError",{value:!0});me.from=(n,i,s,l,c,d)=>{const f=Object.create(fh);return O.toFlatObject(n,f,function(x){return x!==Error.prototype},m=>m!=="isAxiosError"),me.call(f,n.message,i,s,l,c),f.cause=n,f.name=n.name,d&&Object.assign(f,d),f};const W0=null;function lu(n){return O.isPlainObject(n)||O.isArray(n)}function hh(n){return O.endsWith(n,"[]")?n.slice(0,-2):n}function Jf(n,i,s){return n?n.concat(i).map(function(c,d){return c=hh(c),!s&&d?"["+c+"]":c}).join(s?".":""):i}function q0(n){return O.isArray(n)&&!n.some(lu)}const Q0=O.toFlatObject(O,{},null,function(i){return/^is[A-Z]/.test(i)});function $s(n,i,s){if(!O.isObject(n))throw new TypeError("target must be an object");i=i||new FormData,s=O.toFlatObject(s,{metaTokens:!0,dots:!1,indexes:!1},!1,function(A,_){return!O.isUndefined(_[A])});const l=s.metaTokens,c=s.visitor||S,d=s.dots,f=s.indexes,x=(s.Blob||typeof Blob<"u"&&Blob)&&O.isSpecCompliantForm(i);if(!O.isFunction(c))throw new TypeError("visitor must be a function");function y(k){if(k===null)return"";if(O.isDate(k))return k.toISOString();if(!x&&O.isBlob(k))throw new me("Blob is not supported. Use a Buffer instead.");return O.isArrayBuffer(k)||O.isTypedArray(k)?x&&typeof Blob=="function"?new Blob([k]):Buffer.from(k):k}function S(k,A,_){let J=k;if(k&&!_&&typeof k=="object"){if(O.endsWith(A,"{}"))A=l?A:A.slice(0,-2),k=JSON.stringify(k);else if(O.isArray(k)&&q0(k)||(O.isFileList(k)||O.endsWith(A,"[]"))&&(J=O.toArray(k)))return A=hh(A),J.forEach(function(H,X){!(O.isUndefined(H)||H===null)&&i.append(f===!0?Jf([A],X,d):f===null?A:A+"[]",y(H))}),!1}return lu(k)?!0:(i.append(Jf(_,A,d),y(k)),!1)}const j=[],$=Object.assign(Q0,{defaultVisitor:S,convertValue:y,isVisitable:lu});function I(k,A){if(!O.isUndefined(k)){if(j.indexOf(k)!==-1)throw Error("Circular reference detected in "+A.join("."));j.push(k),O.forEach(k,function(J,G){(!(O.isUndefined(J)||J===null)&&c.call(i,J,O.isString(G)?G.trim():G,A,$))===!0&&I(J,A?A.concat(G):[G])}),j.pop()}}if(!O.isObject(n))throw new TypeError("data must be an object");return I(n),i}function Zf(n){const i={"!":"%21","'":"%27","(":"%28",")":"%29","~":"%7E","%20":"+","%00":"\0"};return encodeURIComponent(n).replace(/[!'()~]|%20|%00/g,function(l){return i[l]})}function Cu(n,i){this._pairs=[],n&&$s(n,this,i)}const mh=Cu.prototype;mh.append=function(i,s){this._pairs.push([i,s])};mh.toString=function(i){const s=i?function(l){return i.call(this,l,Zf)}:Zf;return this._pairs.map(function(c){return s(c[0])+"="+s(c[1])},"").join("&")};function G0(n){return encodeURIComponent(n).replace(/%3A/gi,":").replace(/%24/g,"$").replace(/%2C/gi,",").replace(/%20/g,"+").replace(/%5B/gi,"[").replace(/%5D/gi,"]")}function gh(n,i,s){if(!i)return n;const l=s&&s.encode||G0;O.isFunction(s)&&(s={serialize:s});const c=s&&s.serialize;let d;if(c?d=c(i,s):d=O.isURLSearchParams(i)?i.toString():new Cu(i,s).toString(l),d){const f=n.indexOf("#");f!==-1&&(n=n.slice(0,f)),n+=(n.indexOf("?")===-1?"?":"&")+d}return n}class ep{constructor(){this.handlers=[]}use(i,s,l){return this.handlers.push({fulfilled:i,rejected:s,synchronous:l?l.synchronous:!1,runWhen:l?l.runWhen:null}),this.handlers.length-1}eject(i){this.handlers[i]&&(this.handlers[i]=null)}clear(){this.handlers&&(this.handlers=[])}forEach(i){O.forEach(this.handlers,function(l){l!==null&&i(l)})}}const yh={silentJSONParsing:!0,forcedJSONParsing:!0,clarifyTimeoutError:!1},K0=typeof URLSearchParams<"u"?URLSearchParams:Cu,X0=typeof FormData<"u"?FormData:null,J0=typeof Blob<"u"?Blob:null,Z0={isBrowser:!0,classes:{URLSearchParams:K0,FormData:X0,Blob:J0},protocols:["http","https","file","blob","url","data"]},Eu=typeof window<"u"&&typeof document<"u",au=typeof navigator=="object"&&navigator||void 0,ex=Eu&&(!au||["ReactNative","NativeScript","NS"].indexOf(au.product)<0),tx=typeof WorkerGlobalScope<"u"&&self instanceof WorkerGlobalScope&&typeof self.importScripts=="function",nx=Eu&&window.location.href||"http://localhost",rx=Object.freeze(Object.defineProperty({__proto__:null,hasBrowserEnv:Eu,hasStandardBrowserEnv:ex,hasStandardBrowserWebWorkerEnv:tx,navigator:au,origin:nx},Symbol.toStringTag,{value:"Module"})),it={...rx,...Z0};function ox(n,i){return $s(n,new it.classes.URLSearchParams,Object.assign({visitor:function(s,l,c,d){return it.isNode&&O.isBuffer(s)?(this.append(l,s.toString("base64")),!1):d.defaultVisitor.apply(this,arguments)}},i))}function ix(n){return O.matchAll(/\w+|\[(\w*)]/g,n).map(i=>i[0]==="[]"?"":i[1]||i[0])}function sx(n){const i={},s=Object.keys(n);let l;const c=s.length;let d;for(l=0;l<c;l++)d=s[l],i[d]=n[d];return i}function xh(n){function i(s,l,c,d){let f=s[d++];if(f==="__proto__")return!0;const m=Number.isFinite(+f),x=d>=s.length;return f=!f&&O.isArray(c)?c.length:f,x?(O.hasOwnProp(c,f)?c[f]=[c[f],l]:c[f]=l,!m):((!c[f]||!O.isObject(c[f]))&&(c[f]=[]),i(s,l,c[f],d)&&O.isArray(c[f])&&(c[f]=sx(c[f])),!m)}if(O.isFormData(n)&&O.isFunction(n.entries)){const s={};return O.forEachEntry(n,(l,c)=>{i(ix(l),c,s,0)}),s}return null}function lx(n,i,s){if(O.isString(n))try{return(i||JSON.parse)(n),O.trim(n)}catch(l){if(l.name!=="SyntaxError")throw l}return(0,JSON.stringify)(n)}const zo={transitional:yh,adapter:["xhr","http","fetch"],transformRequest:[function(i,s){const l=s.getContentType()||"",c=l.indexOf("application/json")>-1,d=O.isObject(i);if(d&&O.isHTMLForm(i)&&(i=new FormData(i)),O.isFormData(i))return c?JSON.stringify(xh(i)):i;if(O.isArrayBuffer(i)||O.isBuffer(i)||O.isStream(i)||O.isFile(i)||O.isBlob(i)||O.isReadableStream(i))return i;if(O.isArrayBufferView(i))return i.buffer;if(O.isURLSearchParams(i))return s.setContentType("application/x-www-form-urlencoded;charset=utf-8",!1),i.toString();let m;if(d){if(l.indexOf("application/x-www-form-urlencoded")>-1)return ox(i,this.formSerializer).toString();if((m=O.isFileList(i))||l.indexOf("multipart/form-data")>-1){const x=this.env&&this.env.FormData;return $s(m?{"files[]":i}:i,x&&new x,this.formSerializer)}}return d||c?(s.setContentType("application/json",!1),lx(i)):i}],transformResponse:[function(i){const s=this.transitional||zo.transitional,l=s&&s.forcedJSONParsing,c=this.responseType==="json";if(O.isResponse(i)||O.isReadableStream(i))return i;if(i&&O.isString(i)&&(l&&!this.responseType||c)){const f=!(s&&s.silentJSONParsing)&&c;try{return JSON.parse(i)}catch(m){if(f)throw m.name==="SyntaxError"?me.from(m,me.ERR_BAD_RESPONSE,this,null,this.response):m}}return i}],timeout:0,xsrfCookieName:"XSRF-TOKEN",xsrfHeaderName:"X-XSRF-TOKEN",maxContentLength:-1,maxBodyLength:-1,env:{FormData:it.classes.FormData,Blob:it.classes.Blob},validateStatus:function(i){return i>=200&&i<300},headers:{common:{Accept:"application/json, text/plain, */*","Content-Type":void 0}}};O.forEach(["delete","get","head","post","put","patch"],n=>{zo.headers[n]={}});const ax=O.toObjectSet(["age","authorization","content-length","content-type","etag","expires","from","host","if-modified-since","if-unmodified-since","last-modified","location","max-forwards","proxy-authorization","referer","retry-after","user-agent"]),ux=n=>{const i={};let s,l,c;return n&&n.split(`
`).forEach(function(f){c=f.indexOf(":"),s=f.substring(0,c).trim().toLowerCase(),l=f.substring(c+1).trim(),!(!s||i[s]&&ax[s])&&(s==="set-cookie"?i[s]?i[s].push(l):i[s]=[l]:i[s]=i[s]?i[s]+", "+l:l)}),i},tp=Symbol("internals");function Eo(n){return n&&String(n).trim().toLowerCase()}function fs(n){return n===!1||n==null?n:O.isArray(n)?n.map(fs):String(n)}function cx(n){const i=Object.create(null),s=/([^\s,;=]+)\s*(?:=\s*([^,;]+))?/g;let l;for(;l=s.exec(n);)i[l[1]]=l[2];return i}const dx=n=>/^[-_a-zA-Z0-9^`|~,!#$%&'*+.]+$/.test(n.trim());function za(n,i,s,l,c){if(O.isFunction(l))return l.call(this,i,s);if(c&&(i=s),!!O.isString(i)){if(O.isString(l))return i.indexOf(l)!==-1;if(O.isRegExp(l))return l.test(i)}}function fx(n){return n.trim().toLowerCase().replace(/([a-z\d])(\w*)/g,(i,s,l)=>s.toUpperCase()+l)}function px(n,i){const s=O.toCamelCase(" "+i);["get","set","has"].forEach(l=>{Object.defineProperty(n,l+s,{value:function(c,d,f){return this[l].call(this,i,c,d,f)},configurable:!0})})}class mt{constructor(i){i&&this.set(i)}set(i,s,l){const c=this;function d(m,x,y){const S=Eo(x);if(!S)throw new Error("header name must be a non-empty string");const j=O.findKey(c,S);(!j||c[j]===void 0||y===!0||y===void 0&&c[j]!==!1)&&(c[j||x]=fs(m))}const f=(m,x)=>O.forEach(m,(y,S)=>d(y,S,x));if(O.isPlainObject(i)||i instanceof this.constructor)f(i,s);else if(O.isString(i)&&(i=i.trim())&&!dx(i))f(ux(i),s);else if(O.isHeaders(i))for(const[m,x]of i.entries())d(x,m,l);else i!=null&&d(s,i,l);return this}get(i,s){if(i=Eo(i),i){const l=O.findKey(this,i);if(l){const c=this[l];if(!s)return c;if(s===!0)return cx(c);if(O.isFunction(s))return s.call(this,c,l);if(O.isRegExp(s))return s.exec(c);throw new TypeError("parser must be boolean|regexp|function")}}}has(i,s){if(i=Eo(i),i){const l=O.findKey(this,i);return!!(l&&this[l]!==void 0&&(!s||za(this,this[l],l,s)))}return!1}delete(i,s){const l=this;let c=!1;function d(f){if(f=Eo(f),f){const m=O.findKey(l,f);m&&(!s||za(l,l[m],m,s))&&(delete l[m],c=!0)}}return O.isArray(i)?i.forEach(d):d(i),c}clear(i){const s=Object.keys(this);let l=s.length,c=!1;for(;l--;){const d=s[l];(!i||za(this,this[d],d,i,!0))&&(delete this[d],c=!0)}return c}normalize(i){const s=this,l={};return O.forEach(this,(c,d)=>{const f=O.findKey(l,d);if(f){s[f]=fs(c),delete s[d];return}const m=i?fx(d):String(d).trim();m!==d&&delete s[d],s[m]=fs(c),l[m]=!0}),this}concat(...i){return this.constructor.concat(this,...i)}toJSON(i){const s=Object.create(null);return O.forEach(this,(l,c)=>{l!=null&&l!==!1&&(s[c]=i&&O.isArray(l)?l.join(", "):l)}),s}[Symbol.iterator](){return Object.entries(this.toJSON())[Symbol.iterator]()}toString(){return Object.entries(this.toJSON()).map(([i,s])=>i+": "+s).join(`
`)}get[Symbol.toStringTag](){return"AxiosHeaders"}static from(i){return i instanceof this?i:new this(i)}static concat(i,...s){const l=new this(i);return s.forEach(c=>l.set(c)),l}static accessor(i){const l=(this[tp]=this[tp]={accessors:{}}).accessors,c=this.prototype;function d(f){const m=Eo(f);l[m]||(px(c,f),l[m]=!0)}return O.isArray(i)?i.forEach(d):d(i),this}}mt.accessor(["Content-Type","Content-Length","Accept","Accept-Encoding","User-Agent","Authorization"]);O.reduceDescriptors(mt.prototype,({value:n},i)=>{let s=i[0].toUpperCase()+i.slice(1);return{get:()=>n,set(l){this[s]=l}}});O.freezeMethods(mt);function Ba(n,i){const s=this||zo,l=i||s,c=mt.from(l.headers);let d=l.data;return O.forEach(n,function(m){d=m.call(s,d,c.normalize(),i?i.status:void 0)}),c.normalize(),d}function vh(n){return!!(n&&n.__CANCEL__)}function Lr(n,i,s){me.call(this,n??"canceled",me.ERR_CANCELED,i,s),this.name="CanceledError"}O.inherits(Lr,me,{__CANCEL__:!0});function wh(n,i,s){const l=s.config.validateStatus;!s.status||!l||l(s.status)?n(s):i(new me("Request failed with status code "+s.status,[me.ERR_BAD_REQUEST,me.ERR_BAD_RESPONSE][Math.floor(s.status/100)-4],s.config,s.request,s))}function hx(n){const i=/^([-+\w]{1,25})(:?\/\/|:)/.exec(n);return i&&i[1]||""}function mx(n,i){n=n||10;const s=new Array(n),l=new Array(n);let c=0,d=0,f;return i=i!==void 0?i:1e3,function(x){const y=Date.now(),S=l[d];f||(f=y),s[c]=x,l[c]=y;let j=d,$=0;for(;j!==c;)$+=s[j++],j=j%n;if(c=(c+1)%n,c===d&&(d=(d+1)%n),y-f<i)return;const I=S&&y-S;return I?Math.round($*1e3/I):void 0}}function gx(n,i){let s=0,l=1e3/i,c,d;const f=(y,S=Date.now())=>{s=S,c=null,d&&(clearTimeout(d),d=null),n.apply(null,y)};return[(...y)=>{const S=Date.now(),j=S-s;j>=l?f(y,S):(c=y,d||(d=setTimeout(()=>{d=null,f(c)},l-j)))},()=>c&&f(c)]}const ks=(n,i,s=3)=>{let l=0;const c=mx(50,250);return gx(d=>{const f=d.loaded,m=d.lengthComputable?d.total:void 0,x=f-l,y=c(x),S=f<=m;l=f;const j={loaded:f,total:m,progress:m?f/m:void 0,bytes:x,rate:y||void 0,estimated:y&&m&&S?(m-f)/y:void 0,event:d,lengthComputable:m!=null,[i?"download":"upload"]:!0};n(j)},s)},np=(n,i)=>{const s=n!=null;return[l=>i[0]({lengthComputable:s,total:n,loaded:l}),i[1]]},rp=n=>(...i)=>O.asap(()=>n(...i)),yx=it.hasStandardBrowserEnv?((n,i)=>s=>(s=new URL(s,it.origin),n.protocol===s.protocol&&n.host===s.host&&(i||n.port===s.port)))(new URL(it.origin),it.navigator&&/(msie|trident)/i.test(it.navigator.userAgent)):()=>!0,xx=it.hasStandardBrowserEnv?{write(n,i,s,l,c,d){const f=[n+"="+encodeURIComponent(i)];O.isNumber(s)&&f.push("expires="+new Date(s).toGMTString()),O.isString(l)&&f.push("path="+l),O.isString(c)&&f.push("domain="+c),d===!0&&f.push("secure"),document.cookie=f.join("; ")},read(n){const i=document.cookie.match(new RegExp("(^|;\\s*)("+n+")=([^;]*)"));return i?decodeURIComponent(i[3]):null},remove(n){this.write(n,"",Date.now()-864e5)}}:{write(){},read(){return null},remove(){}};function vx(n){return/^([a-z][a-z\d+\-.]*:)?\/\//i.test(n)}function wx(n,i){return i?n.replace(/\/?\/$/,"")+"/"+i.replace(/^\/+/,""):n}function Sh(n,i){return n&&!vx(i)?wx(n,i):i}const op=n=>n instanceof mt?{...n}:n;function Gn(n,i){i=i||{};const s={};function l(y,S,j,$){return O.isPlainObject(y)&&O.isPlainObject(S)?O.merge.call({caseless:$},y,S):O.isPlainObject(S)?O.merge({},S):O.isArray(S)?S.slice():S}function c(y,S,j,$){if(O.isUndefined(S)){if(!O.isUndefined(y))return l(void 0,y,j,$)}else return l(y,S,j,$)}function d(y,S){if(!O.isUndefined(S))return l(void 0,S)}function f(y,S){if(O.isUndefined(S)){if(!O.isUndefined(y))return l(void 0,y)}else return l(void 0,S)}function m(y,S,j){if(j in i)return l(y,S);if(j in n)return l(void 0,y)}const x={url:d,method:d,data:d,baseURL:f,transformRequest:f,transformResponse:f,paramsSerializer:f,timeout:f,timeoutMessage:f,withCredentials:f,withXSRFToken:f,adapter:f,responseType:f,xsrfCookieName:f,xsrfHeaderName:f,onUploadProgress:f,onDownloadProgress:f,decompress:f,maxContentLength:f,maxBodyLength:f,beforeRedirect:f,transport:f,httpAgent:f,httpsAgent:f,cancelToken:f,socketPath:f,responseEncoding:f,validateStatus:m,headers:(y,S,j)=>c(op(y),op(S),j,!0)};return O.forEach(Object.keys(Object.assign({},n,i)),function(S){const j=x[S]||c,$=j(n[S],i[S],S);O.isUndefined($)&&j!==m||(s[S]=$)}),s}const kh=n=>{const i=Gn({},n);let{data:s,withXSRFToken:l,xsrfHeaderName:c,xsrfCookieName:d,headers:f,auth:m}=i;i.headers=f=mt.from(f),i.url=gh(Sh(i.baseURL,i.url),n.params,n.paramsSerializer),m&&f.set("Authorization","Basic "+btoa((m.username||"")+":"+(m.password?unescape(encodeURIComponent(m.password)):"")));let x;if(O.isFormData(s)){if(it.hasStandardBrowserEnv||it.hasStandardBrowserWebWorkerEnv)f.setContentType(void 0);else if((x=f.getContentType())!==!1){const[y,...S]=x?x.split(";").map(j=>j.trim()).filter(Boolean):[];f.setContentType([y||"multipart/form-data",...S].join("; "))}}if(it.hasStandardBrowserEnv&&(l&&O.isFunction(l)&&(l=l(i)),l||l!==!1&&yx(i.url))){const y=c&&d&&xx.read(d);y&&f.set(c,y)}return i},Sx=typeof XMLHttpRequest<"u",kx=Sx&&function(n){return new Promise(function(s,l){const c=kh(n);let d=c.data;const f=mt.from(c.headers).normalize();let{responseType:m,onUploadProgress:x,onDownloadProgress:y}=c,S,j,$,I,k;function A(){I&&I(),k&&k(),c.cancelToken&&c.cancelToken.unsubscribe(S),c.signal&&c.signal.removeEventListener("abort",S)}let _=new XMLHttpRequest;_.open(c.method.toUpperCase(),c.url,!0),_.timeout=c.timeout;function J(){if(!_)return;const H=mt.from("getAllResponseHeaders"in _&&_.getAllResponseHeaders()),D={data:!m||m==="text"||m==="json"?_.responseText:_.response,status:_.status,statusText:_.statusText,headers:H,config:n,request:_};wh(function(Q){s(Q),A()},function(Q){l(Q),A()},D),_=null}"onloadend"in _?_.onloadend=J:_.onreadystatechange=function(){!_||_.readyState!==4||_.status===0&&!(_.responseURL&&_.responseURL.indexOf("file:")===0)||setTimeout(J)},_.onabort=function(){_&&(l(new me("Request aborted",me.ECONNABORTED,n,_)),_=null)},_.onerror=function(){l(new me("Network Error",me.ERR_NETWORK,n,_)),_=null},_.ontimeout=function(){let X=c.timeout?"timeout of "+c.timeout+"ms exceeded":"timeout exceeded";const D=c.transitional||yh;c.timeoutErrorMessage&&(X=c.timeoutErrorMessage),l(new me(X,D.clarifyTimeoutError?me.ETIMEDOUT:me.ECONNABORTED,n,_)),_=null},d===void 0&&f.setContentType(null),"setRequestHeader"in _&&O.forEach(f.toJSON(),function(X,D){_.setRequestHeader(D,X)}),O.isUndefined(c.withCredentials)||(_.withCredentials=!!c.withCredentials),m&&m!=="json"&&(_.responseType=c.responseType),y&&([$,k]=ks(y,!0),_.addEventListener("progress",$)),x&&_.upload&&([j,I]=ks(x),_.upload.addEventListener("progress",j),_.upload.addEventListener("loadend",I)),(c.cancelToken||c.signal)&&(S=H=>{_&&(l(!H||H.type?new Lr(null,n,_):H),_.abort(),_=null)},c.cancelToken&&c.cancelToken.subscribe(S),c.signal&&(c.signal.aborted?S():c.signal.addEventListener("abort",S)));const G=hx(c.url);if(G&&it.protocols.indexOf(G)===-1){l(new me("Unsupported protocol "+G+":",me.ERR_BAD_REQUEST,n));return}_.send(d||null)})},Cx=(n,i)=>{const{length:s}=n=n?n.filter(Boolean):[];if(i||s){let l=new AbortController,c;const d=function(y){if(!c){c=!0,m();const S=y instanceof Error?y:this.reason;l.abort(S instanceof me?S:new Lr(S instanceof Error?S.message:S))}};let f=i&&setTimeout(()=>{f=null,d(new me(`timeout ${i} of ms exceeded`,me.ETIMEDOUT))},i);const m=()=>{n&&(f&&clearTimeout(f),f=null,n.forEach(y=>{y.unsubscribe?y.unsubscribe(d):y.removeEventListener("abort",d)}),n=null)};n.forEach(y=>y.addEventListener("abort",d));const{signal:x}=l;return x.unsubscribe=()=>O.asap(m),x}},Ex=function*(n,i){let s=n.byteLength;if(s<i){yield n;return}let l=0,c;for(;l<s;)c=l+i,yield n.slice(l,c),l=c},jx=async function*(n,i){for await(const s of Ax(n))yield*Ex(s,i)},Ax=async function*(n){if(n[Symbol.asyncIterator]){yield*n;return}const i=n.getReader();try{for(;;){const{done:s,value:l}=await i.read();if(s)break;yield l}}finally{await i.cancel()}},ip=(n,i,s,l)=>{const c=jx(n,i);let d=0,f,m=x=>{f||(f=!0,l&&l(x))};return new ReadableStream({async pull(x){try{const{done:y,value:S}=await c.next();if(y){m(),x.close();return}let j=S.byteLength;if(s){let $=d+=j;s($)}x.enqueue(new Uint8Array(S))}catch(y){throw m(y),y}},cancel(x){return m(x),c.return()}},{highWaterMark:2})},Os=typeof fetch=="function"&&typeof Request=="function"&&typeof Response=="function",Ch=Os&&typeof ReadableStream=="function",Rx=Os&&(typeof TextEncoder=="function"?(n=>i=>n.encode(i))(new TextEncoder):async n=>new Uint8Array(await new Response(n).arrayBuffer())),Eh=(n,...i)=>{try{return!!n(...i)}catch{return!1}},Px=Ch&&Eh(()=>{let n=!1;const i=new Request(it.origin,{body:new ReadableStream,method:"POST",get duplex(){return n=!0,"half"}}).headers.has("Content-Type");return n&&!i}),sp=64*1024,uu=Ch&&Eh(()=>O.isReadableStream(new Response("").body)),Cs={stream:uu&&(n=>n.body)};Os&&(n=>{["text","arrayBuffer","blob","formData","stream"].forEach(i=>{!Cs[i]&&(Cs[i]=O.isFunction(n[i])?s=>s[i]():(s,l)=>{throw new me(`Response type '${i}' is not supported`,me.ERR_NOT_SUPPORT,l)})})})(new Response);const Mx=async n=>{if(n==null)return 0;if(O.isBlob(n))return n.size;if(O.isSpecCompliantForm(n))return(await new Request(it.origin,{method:"POST",body:n}).arrayBuffer()).byteLength;if(O.isArrayBufferView(n)||O.isArrayBuffer(n))return n.byteLength;if(O.isURLSearchParams(n)&&(n=n+""),O.isString(n))return(await Rx(n)).byteLength},_x=async(n,i)=>{const s=O.toFiniteNumber(n.getContentLength());return s??Mx(i)},Tx=Os&&(async n=>{let{url:i,method:s,data:l,signal:c,cancelToken:d,timeout:f,onDownloadProgress:m,onUploadProgress:x,responseType:y,headers:S,withCredentials:j="same-origin",fetchOptions:$}=kh(n);y=y?(y+"").toLowerCase():"text";let I=Cx([c,d&&d.toAbortSignal()],f),k;const A=I&&I.unsubscribe&&(()=>{I.unsubscribe()});let _;try{if(x&&Px&&s!=="get"&&s!=="head"&&(_=await _x(S,l))!==0){let D=new Request(i,{method:"POST",body:l,duplex:"half"}),N;if(O.isFormData(l)&&(N=D.headers.get("content-type"))&&S.setContentType(N),D.body){const[Q,le]=np(_,ks(rp(x)));l=ip(D.body,sp,Q,le)}}O.isString(j)||(j=j?"include":"omit");const J="credentials"in Request.prototype;k=new Request(i,{...$,signal:I,method:s.toUpperCase(),headers:S.normalize().toJSON(),body:l,duplex:"half",credentials:J?j:void 0});let G=await fetch(k);const H=uu&&(y==="stream"||y==="response");if(uu&&(m||H&&A)){const D={};["status","statusText","headers"].forEach(Se=>{D[Se]=G[Se]});const N=O.toFiniteNumber(G.headers.get("content-length")),[Q,le]=m&&np(N,ks(rp(m),!0))||[];G=new Response(ip(G.body,sp,Q,()=>{le&&le(),A&&A()}),D)}y=y||"text";let X=await Cs[O.findKey(Cs,y)||"text"](G,n);return!H&&A&&A(),await new Promise((D,N)=>{wh(D,N,{data:X,headers:mt.from(G.headers),status:G.status,statusText:G.statusText,config:n,request:k})})}catch(J){throw A&&A(),J&&J.name==="TypeError"&&/fetch/i.test(J.message)?Object.assign(new me("Network Error",me.ERR_NETWORK,n,k),{cause:J.cause||J}):me.from(J,J&&J.code,n,k)}}),cu={http:W0,xhr:kx,fetch:Tx};O.forEach(cu,(n,i)=>{if(n){try{Object.defineProperty(n,"name",{value:i})}catch{}Object.defineProperty(n,"adapterName",{value:i})}});const lp=n=>`- ${n}`,Nx=n=>O.isFunction(n)||n===null||n===!1,jh={getAdapter:n=>{n=O.isArray(n)?n:[n];const{length:i}=n;let s,l;const c={};for(let d=0;d<i;d++){s=n[d];let f;if(l=s,!Nx(s)&&(l=cu[(f=String(s)).toLowerCase()],l===void 0))throw new me(`Unknown adapter '${f}'`);if(l)break;c[f||"#"+d]=l}if(!l){const d=Object.entries(c).map(([m,x])=>`adapter ${m} `+(x===!1?"is not supported by the environment":"is not available in the build"));let f=i?d.length>1?`since :
`+d.map(lp).join(`
`):" "+lp(d[0]):"as no adapter specified";throw new me("There is no suitable adapter to dispatch the request "+f,"ERR_NOT_SUPPORT")}return l},adapters:cu};function Fa(n){if(n.cancelToken&&n.cancelToken.throwIfRequested(),n.signal&&n.signal.aborted)throw new Lr(null,n)}function ap(n){return Fa(n),n.headers=mt.from(n.headers),n.data=Ba.call(n,n.transformRequest),["post","put","patch"].indexOf(n.method)!==-1&&n.headers.setContentType("application/x-www-form-urlencoded",!1),jh.getAdapter(n.adapter||zo.adapter)(n).then(function(l){return Fa(n),l.data=Ba.call(n,n.transformResponse,l),l.headers=mt.from(l.headers),l},function(l){return vh(l)||(Fa(n),l&&l.response&&(l.response.data=Ba.call(n,n.transformResponse,l.response),l.response.headers=mt.from(l.response.headers))),Promise.reject(l)})}const Ah="1.7.9",Ls={};["object","boolean","number","function","string","symbol"].forEach((n,i)=>{Ls[n]=function(l){return typeof l===n||"a"+(i<1?"n ":" ")+n}});const up={};Ls.transitional=function(i,s,l){function c(d,f){return"[Axios v"+Ah+"] Transitional option '"+d+"'"+f+(l?". "+l:"")}return(d,f,m)=>{if(i===!1)throw new me(c(f," has been removed"+(s?" in "+s:"")),me.ERR_DEPRECATED);return s&&!up[f]&&(up[f]=!0,console.warn(c(f," has been deprecated since v"+s+" and will be removed in the near future"))),i?i(d,f,m):!0}};Ls.spelling=function(i){return(s,l)=>(console.warn(`${l} is likely a misspelling of ${i}`),!0)};function $x(n,i,s){if(typeof n!="object")throw new me("options must be an object",me.ERR_BAD_OPTION_VALUE);const l=Object.keys(n);let c=l.length;for(;c-- >0;){const d=l[c],f=i[d];if(f){const m=n[d],x=m===void 0||f(m,d,n);if(x!==!0)throw new me("option "+d+" must be "+x,me.ERR_BAD_OPTION_VALUE);continue}if(s!==!0)throw new me("Unknown option "+d,me.ERR_BAD_OPTION)}}const ps={assertOptions:$x,validators:Ls},Vt=ps.validators;class Wn{constructor(i){this.defaults=i,this.interceptors={request:new ep,response:new ep}}async request(i,s){try{return await this._request(i,s)}catch(l){if(l instanceof Error){let c={};Error.captureStackTrace?Error.captureStackTrace(c):c=new Error;const d=c.stack?c.stack.replace(/^.+\n/,""):"";try{l.stack?d&&!String(l.stack).endsWith(d.replace(/^.+\n.+\n/,""))&&(l.stack+=`
`+d):l.stack=d}catch{}}throw l}}_request(i,s){typeof i=="string"?(s=s||{},s.url=i):s=i||{},s=Gn(this.defaults,s);const{transitional:l,paramsSerializer:c,headers:d}=s;l!==void 0&&ps.assertOptions(l,{silentJSONParsing:Vt.transitional(Vt.boolean),forcedJSONParsing:Vt.transitional(Vt.boolean),clarifyTimeoutError:Vt.transitional(Vt.boolean)},!1),c!=null&&(O.isFunction(c)?s.paramsSerializer={serialize:c}:ps.assertOptions(c,{encode:Vt.function,serialize:Vt.function},!0)),ps.assertOptions(s,{baseUrl:Vt.spelling("baseURL"),withXsrfToken:Vt.spelling("withXSRFToken")},!0),s.method=(s.method||this.defaults.method||"get").toLowerCase();let f=d&&O.merge(d.common,d[s.method]);d&&O.forEach(["delete","get","head","post","put","patch","common"],k=>{delete d[k]}),s.headers=mt.concat(f,d);const m=[];let x=!0;this.interceptors.request.forEach(function(A){typeof A.runWhen=="function"&&A.runWhen(s)===!1||(x=x&&A.synchronous,m.unshift(A.fulfilled,A.rejected))});const y=[];this.interceptors.response.forEach(function(A){y.push(A.fulfilled,A.rejected)});let S,j=0,$;if(!x){const k=[ap.bind(this),void 0];for(k.unshift.apply(k,m),k.push.apply(k,y),$=k.length,S=Promise.resolve(s);j<$;)S=S.then(k[j++],k[j++]);return S}$=m.length;let I=s;for(j=0;j<$;){const k=m[j++],A=m[j++];try{I=k(I)}catch(_){A.call(this,_);break}}try{S=ap.call(this,I)}catch(k){return Promise.reject(k)}for(j=0,$=y.length;j<$;)S=S.then(y[j++],y[j++]);return S}getUri(i){i=Gn(this.defaults,i);const s=Sh(i.baseURL,i.url);return gh(s,i.params,i.paramsSerializer)}}O.forEach(["delete","get","head","options"],function(i){Wn.prototype[i]=function(s,l){return this.request(Gn(l||{},{method:i,url:s,data:(l||{}).data}))}});O.forEach(["post","put","patch"],function(i){function s(l){return function(d,f,m){return this.request(Gn(m||{},{method:i,headers:l?{"Content-Type":"multipart/form-data"}:{},url:d,data:f}))}}Wn.prototype[i]=s(),Wn.prototype[i+"Form"]=s(!0)});class ju{constructor(i){if(typeof i!="function")throw new TypeError("executor must be a function.");let s;this.promise=new Promise(function(d){s=d});const l=this;this.promise.then(c=>{if(!l._listeners)return;let d=l._listeners.length;for(;d-- >0;)l._listeners[d](c);l._listeners=null}),this.promise.then=c=>{let d;const f=new Promise(m=>{l.subscribe(m),d=m}).then(c);return f.cancel=function(){l.unsubscribe(d)},f},i(function(d,f,m){l.reason||(l.reason=new Lr(d,f,m),s(l.reason))})}throwIfRequested(){if(this.reason)throw this.reason}subscribe(i){if(this.reason){i(this.reason);return}this._listeners?this._listeners.push(i):this._listeners=[i]}unsubscribe(i){if(!this._listeners)return;const s=this._listeners.indexOf(i);s!==-1&&this._listeners.splice(s,1)}toAbortSignal(){const i=new AbortController,s=l=>{i.abort(l)};return this.subscribe(s),i.signal.unsubscribe=()=>this.unsubscribe(s),i.signal}static source(){let i;return{token:new ju(function(c){i=c}),cancel:i}}}function Ox(n){return function(s){return n.apply(null,s)}}function Lx(n){return O.isObject(n)&&n.isAxiosError===!0}const du={Continue:100,SwitchingProtocols:101,Processing:102,EarlyHints:103,Ok:200,Created:201,Accepted:202,NonAuthoritativeInformation:203,NoContent:204,ResetContent:205,PartialContent:206,MultiStatus:207,AlreadyReported:208,ImUsed:226,MultipleChoices:300,MovedPermanently:301,Found:302,SeeOther:303,NotModified:304,UseProxy:305,Unused:306,TemporaryRedirect:307,PermanentRedirect:308,BadRequest:400,Unauthorized:401,PaymentRequired:402,Forbidden:403,NotFound:404,MethodNotAllowed:405,NotAcceptable:406,ProxyAuthenticationRequired:407,RequestTimeout:408,Conflict:409,Gone:410,LengthRequired:411,PreconditionFailed:412,PayloadTooLarge:413,UriTooLong:414,UnsupportedMediaType:415,RangeNotSatisfiable:416,ExpectationFailed:417,ImATeapot:418,MisdirectedRequest:421,UnprocessableEntity:422,Locked:423,FailedDependency:424,TooEarly:425,UpgradeRequired:426,PreconditionRequired:428,TooManyRequests:429,RequestHeaderFieldsTooLarge:431,UnavailableForLegalReasons:451,InternalServerError:500,NotImplemented:501,BadGateway:502,ServiceUnavailable:503,GatewayTimeout:504,HttpVersionNotSupported:505,VariantAlsoNegotiates:506,InsufficientStorage:507,LoopDetected:508,NotExtended:510,NetworkAuthenticationRequired:511};Object.entries(du).forEach(([n,i])=>{du[i]=n});function Rh(n){const i=new Wn(n),s=oh(Wn.prototype.request,i);return O.extend(s,Wn.prototype,i,{allOwnKeys:!0}),O.extend(s,i,null,{allOwnKeys:!0}),s.create=function(c){return Rh(Gn(n,c))},s}const ze=Rh(zo);ze.Axios=Wn;ze.CanceledError=Lr;ze.CancelToken=ju;ze.isCancel=vh;ze.VERSION=Ah;ze.toFormData=$s;ze.AxiosError=me;ze.Cancel=ze.CanceledError;ze.all=function(i){return Promise.all(i)};ze.spread=Ox;ze.isAxiosError=Lx;ze.mergeConfig=Gn;ze.AxiosHeaders=mt;ze.formToJSON=n=>xh(O.isHTMLForm(n)?new FormData(n):n);ze.getAdapter=jh.getAdapter;ze.HttpStatusCode=du;ze.default=ze;const Ph={apiBaseUrl:"/api"};class Dx{constructor(){kf(this,"events",{})}on(i,s){return this.events[i]||(this.events[i]=[]),this.events[i].push(s),()=>this.off(i,s)}off(i,s){this.events[i]&&(this.events[i]=this.events[i].filter(l=>l!==s))}emit(i,s){this.events[i]&&this.events[i].forEach(l=>{l(s)})}}const jr=new Dx,Ix=async(n,i)=>{const s=new FormData;return s.append("username",n),s.append("password",i),(await Bo.post("/auth/login",s,{headers:{"Content-Type":"multipart/form-data"}})).data},bx=async n=>(await Bo.post("/users",n,{headers:{"Content-Type":"multipart/form-data"}})).data,zx=async()=>{await Bo.get("/auth/csrf-token")},Bx=async()=>{await Bo.post("/auth/logout")},Fx=async()=>(await Bo.post("/auth/refresh")).data,Ux=async(n,i)=>{const s={userId:n,newRole:i};return(await Le.put("/auth/role",s)).data},et=Kn((n,i)=>({currentUser:null,accessToken:null,login:async(s,l)=>{const{userDto:c,accessToken:d}=await Ix(s,l);await i().fetchCsrfToken(),n({currentUser:c,accessToken:d})},logout:async()=>{await Bx(),i().clear(),i().fetchCsrfToken()},fetchCsrfToken:async()=>{await zx()},refreshToken:async()=>{i().clear();const{userDto:s,accessToken:l}=await Fx();n({currentUser:s,accessToken:l})},clear:()=>{n({currentUser:null,accessToken:null})},updateUserRole:async(s,l)=>{await Ux(s,l)}}));let jo=[],Zi=!1;const Le=ze.create({baseURL:Ph.apiBaseUrl,headers:{"Content-Type":"application/json"},withCredentials:!0}),Bo=ze.create({baseURL:Ph.apiBaseUrl,headers:{"Content-Type":"application/json"},withCredentials:!0});Le.interceptors.request.use(n=>{const i=et.getState().accessToken;return i&&(n.headers.Authorization=`Bearer ${i}`),n},n=>Promise.reject(n));Le.interceptors.response.use(n=>n,async n=>{var s,l,c,d;const i=(s=n.response)==null?void 0:s.data;if(i){const f=(c=(l=n.response)==null?void 0:l.headers)==null?void 0:c["discodeit-request-id"];f&&(i.requestId=f),n.response.data=i}if(console.log({error:n,errorResponse:i}),jr.emit("api-error",{error:n,alert:((d=n.response)==null?void 0:d.status)===403}),n.response&&n.response.status===401){const f=n.config;if(f&&f.headers&&f.headers._retry)return jr.emit("auth-error"),Promise.reject(n);if(Zi&&f)return new Promise((m,x)=>{jo.push({config:f,resolve:m,reject:x})});if(f){Zi=!0;try{return await et.getState().refreshToken(),jo.forEach(({config:m,resolve:x,reject:y})=>{m.headers=m.headers||{},m.headers._retry="true",Le(m).then(x).catch(y)}),f.headers=f.headers||{},f.headers._retry="true",jo=[],Zi=!1,Le(f)}catch(m){return jo.forEach(({reject:x})=>x(m)),jo=[],Zi=!1,jr.emit("auth-error"),Promise.reject(m)}}}return Promise.reject(n)});const Hx=async(n,i)=>(await Le.patch(`/users/${n}`,i,{headers:{"Content-Type":"multipart/form-data"}})).data,Yx=async()=>(await Le.get("/users")).data,Nr=Kn(n=>({users:[],fetchUsers:async()=>{try{const i=await Yx();n({users:i})}catch(i){console.error("사용자 목록 조회 실패:",i)}}})),ee={colors:{brand:{primary:"#5865F2",hover:"#4752C4"},background:{primary:"#1a1a1a",secondary:"#2a2a2a",tertiary:"#333333",input:"#40444B",hover:"rgba(255, 255, 255, 0.1)"},text:{primary:"#ffffff",secondary:"#cccccc",muted:"#999999"},status:{online:"#43b581",idle:"#faa61a",dnd:"#f04747",offline:"#747f8d",error:"#ED4245"},border:{primary:"#404040"}}},Mh=E.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
`,_h=E.div`
  background: ${ee.colors.background.primary};
  padding: 32px;
  border-radius: 8px;
  width: 440px;

  h2 {
    color: ${ee.colors.text.primary};
    margin-bottom: 24px;
    font-size: 24px;
    font-weight: bold;
  }

  form {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }
`,To=E.input`
  width: 100%;
  padding: 10px;
  border-radius: 4px;
  background: ${ee.colors.background.input};
  border: none;
  color: ${ee.colors.text.primary};
  font-size: 16px;

  &::placeholder {
    color: ${ee.colors.text.muted};
  }

  &:focus {
    outline: none;
  }
`;E.input.attrs({type:"checkbox"})`
  width: 16px;
  height: 16px;
  padding: 0;
  border-radius: 4px;
  background: ${ee.colors.background.input};
  border: none;
  color: ${ee.colors.text.primary};
  cursor: pointer;

  &:focus {
    outline: none;
  }

  &:checked {
    background: ${ee.colors.brand.primary};
  }
`;const Th=E.button`
  width: 100%;
  padding: 12px;
  border-radius: 4px;
  background: ${ee.colors.brand.primary};
  color: white;
  font-size: 16px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;

  &:hover {
    background: ${ee.colors.brand.hover};
  }
`,Nh=E.div`
  color: ${ee.colors.status.error};
  font-size: 14px;
  text-align: center;
`,Vx=E.p`
  text-align: center;
  margin-top: 16px;
  color: ${({theme:n})=>n.colors.text.muted};
  font-size: 14px;
`,Wx=E.span`
  color: ${({theme:n})=>n.colors.brand.primary};
  cursor: pointer;
  
  &:hover {
    text-decoration: underline;
  }
`,es=E.div`
  margin-bottom: 20px;
`,ts=E.label`
  display: block;
  color: ${({theme:n})=>n.colors.text.muted};
  font-size: 12px;
  font-weight: 700;
  margin-bottom: 8px;
`,Ua=E.span`
  color: ${({theme:n})=>n.colors.status.error};
`,qx=E.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 10px 0;
`,Qx=E.img`
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-bottom: 10px;
  object-fit: cover;
`,Gx=E.input`
  display: none;
`,Kx=E.label`
  color: ${({theme:n})=>n.colors.brand.primary};
  cursor: pointer;
  font-size: 14px;
  
  &:hover {
    text-decoration: underline;
  }
`,Xx=E.span`
  color: ${({theme:n})=>n.colors.brand.primary};
  cursor: pointer;
  
  &:hover {
    text-decoration: underline;
  }
`,Jx=E(Xx)`
  display: block;
  text-align: center;
  margin-top: 16px;
`,Ct="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPAAAADwCAYAAAA+VemSAAAACXBIWXMAACE4AAAhOAFFljFgAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAw2SURBVHgB7d3PT1XpHcfxBy5g6hipSMolGViACThxJDbVRZ2FXejKlf9h/4GmC1fTRdkwC8fE0JgyJuICFkCjEA04GeZe6P0cPC0698I95zzPc57v5f1K6DSto3A8n/v9nufXGfrr338+dgBMGnYAzCLAgGEEGDCMAAOGEWDAMAIMGEaAAcMIMGAYAQYMI8CAYQQYMIwAA4YRYMAwAgwYRoABwwgwYBgBBgwjwIBhBBgwjAADhhFgwDACDBhGgAHDCDBgGAEGDCPAgGEEGDCMAAOGEWDAMAIMGEaAAcMIMGAYAQYMI8CAYQQYMIwAA4YRYMAwAgwYRoABwwgwYBgBBgwjwIBhBBgwjAADhhFgwDACDBhGgAHDCDBgGAEGDCPAgGEEGDCMAAOGEWDAMAIMGEaAAcMIMGAYAQYMI8CAYQQYMIwAA4YRYMAwAgwYRoABwwgwYBgBBgwbcTDvyuWh//33w1/1dexwMRBgYxTW5vVh9/vxYTcxPpR9jY0OffZrdt8fu82ttlvfbLv9j4R5kBHgxCmcE1eH3NfTDTc7PfxZte3lJNgjbmlxxK3+1HKrr1oOg4kAJ0pVdnG+4ZqTw7+psEUoxF91Qv/Di1+db/q+ZpvD7g+T6gb04XLyv6mF3//osuqvTmDn3RGdQCAEOCG6+W/ONdzNTnCrhPZLN2Yb2T99hVhdwOLcSOf37f7hknUN4yedgLoGeb3Rdv/qdAIE2S8CnIDzAuGDQrzXeTZee1OtndaHy9LCSOHvU3++vv693nLPX9LS+0KAa6QQLC2o4sb5a1A7rYGtMqPU+l7v3hpx85+qeVnfdH7W2c7z/Pcrh1RjD5gHromq2JOHY9HCK2Ojzk1dL1fhH90fqxzenDoO/X79DMjhbAQ4Mg1OPXl4KauGodrls6j6FaXKq+dZn/IQ13ENBgkBjiRvQR99V2/lmZos9lc+PxOuxdd1uL3gp6pfVDwDR6Ab9cG9Me9VLAZ1CiHpmXhz6yibakJxVODAZpoN9/iBzfCq+sboFkJ/SAwyrlxAujE1WJWSIiO/sYKlxSpTnbEBqnBxVOBA9LybWnjloM8An6ysitc1NCe5FcvgqgVw/85o1OmhItY32n39uqnJuC3/FAEuhavmmcLra77UN7XP2322qRNX494aqvgojqvmUcrhFa1+6tdXkae6tMiEhR3FEWBPNOCTcni1rZCli4OHAHuQ4mjzaewJHlxMI1Wked5Uw7v99ijbwqd/FnVQQ7WmQyiOAFegZ7a736ZzCU820h+7nbfHbnO7XSq4p3+vmHbfMwdcBgGuoO4dNQrZxtaR+08nqNueT73Y2D7qTIW5aLRXGcUR4JL03FtHeBXa9Y2jyhX2PHudiqg/K9ZuoY3t/uan8TkCXIKCG/u5V2Fae9N2a+vtKO2tjqfVnxfj5zw5O4sWugwCXIJa51hiB/e0tfVWdkZX6CrMCHl5BLigWDt0RCc6rrxo1XZQu6rw6qt2tq47FD0G9Lu8E79FgAvIWucIO3QU2B9ftpK4sVWFZ5rDQTYbqHUOcdztRcJCjgLUToauvrqpny4fJlWVlp/5P4BOH1IcbFcdAe6Tght6h5FeiaLwpnZTq5VW2HzN1eYfUoS3OgLcp9sL4cOrkKT6YrI8dFUHnDQYR3j94Rm4D9kLxQLuV009vKdpXbXae00vFdm8UWVZJ3ojwH3QcS+hnn1VifSMaemVoPqeVzqDT6rG2oivQS5dH33l70ZS262w7n04yhae8MrTMAhwH0KNPFsfyNH3vd+pxkwD1Ydn4HOodQ5VfTXHyrMgqiDA55ibCbNJX1VLc6xAFQT4HCEGr9Q6s3wQPhDgM4RqnzWVQusMHwjwGTS66puCS/WFLwT4DCHOKia88IkA96BjTkOcVbzDQgZ4RIB7CBFejTzz7AufCHAPWn3lGwse4BsB7uGa5wqcLS3k7XvwjAD3cOWy84pnX4RAgHvw/QzMLhyEQIC7CLF4Y4+DyxEAAe4iRIB3PzD6DP8IcBejnncPagCL/bAIgQB34fsc5P2PtM8IgwBHcMjJqQiEAHfBm+JhBQGO4IDlkwiEAHdx2PIbuFhv+MPFQ4C7ODx0Xo2OOiAIAhwBz9QIhQB34XvOlhYaoRDgLg5+dl7pcACqMEIgwF2EWDV1bZwAwz8C3IVOzfAd4omrXGr4x13Vg++jb6YmudTwj7uqh733fgOsM6YZzIJvBLiH3Q/+NyDMB3pNCy4u3k7Yw+57/wNZM9PDbu2NGwjqJiauDrmvpxufXiv6+f+v63fw8SjrZDgLLBwC3INO0NBAls+2V220jurZNXw6h8K6ODfibsye/UjQnNR/nnQcGk/IX/DNsbp+EeAetAVQVaQ56fe5dXGu4X54YTPASwsj7uZ8o/CHmkJ/Y7aRfb3eaBNkj3gGPsNOgNZPN7G1RR36fh8/uJS96LxqR6Kf/9H9MRa2eEKAz7C5FaZS3l6w0/goaArchMeFKPkHwrVxbr+quIJn0LNqiFZPVSjEmx98U7UNVS016PWXe6NU4ooI8DnWN8O8DuX+H0eTnxdeWgjb7uv3/vMd9lpWQYDPEep9Rrp5by+kOy+s7+/mfPhWXyPzFrqRVHHlzpFPgYTwTScg87NphjhmZdTgGMohwH1YexPupdx3b40mN5ij6tuMuHabKlweV60PGo0OdTB7ioM5WjEWW5PNHqVw1fq09ibcu33zqZpUQjzTjN/Ws1urHK5an9bWW0Ffj5JSiOv4HiaYEy6Fq9YnLa1cfRWuCku+wOHmXL2DOnUEmGOHyiHABagKh17Dqxv57rcj7k+3RpKfJ0b9CHBBKy/ivOhIU0yPH4xdqD3EV37HB1ZRBLignc6c8MZW2FY6p5ZSK7b0bNyMOM3CTiE7CHAJz1+2or7vV1Msj74by4IcoyKHOMygH4fhptsHFgEuQRXqx5fx7zYFWRX5ycNL2UqpUFV5512cDuNLvAS9ONawlaQ10jpSJsZ64S+d3iCvm3777XGntW9nx9fsfqh+JK5+Nq0Qi43WvTgCXMHqq5abma53g75Gqmen9fX/alz1CBtNmenfj7k6yvIxQ3Wiha5AN/r3K4fJtX55hVarvVTy8AB9OMV0GGdwf+AQ4IpU4f75LN27Tzt9HtwbKzynrNF2zXvHsvOWClwGAfZAN18dg1r9UnuthSFF6WeK1doS4HIIsCeqVrHbziLUUpdZornc6S5iDC5p8A3FEWCPVn9KO8RlTpVUeJ8u/xLsUAPR780UUjkE2LOUQ6x11jPN4n/l+WDdaqDznEOdO3YREOAAFOJUn4mrTA3p51KQNU/sM8g8/5bHPHAgeibWAND9O2mdtlF147yCm2/o0IeBXlyuAwDKfjDotBMWcJRHBQ5IlUUVa1Bv0O1squnkVSllvd5kAXQVBDiwfBAo5pyqFbo2od5+cVEQ4Ag0CKRnYrWedVfjlLqBlEfsrSDAEWnwJx8Eqsve+zQCrA+SOq/DoCDAkeWDQE+X63k23txKIzRUXz8IcE00Qv23f/wSta3Odim9q/+Zc6Pz3Ev19YNppJrpRtaXXrGinUMhp5zUvqfg+Uu2HvlCgBORB1nzqYtzDTc77ffoHC3CSGEAS4N5zPv6Q4ATo7lVfV253MoWXegMrKob6xWaFKax9PzNdJpfBDhRqlL7n6qy2mqFWeuY9QaDfttsfRCoXd1NYOS5rnPEBh0BNuB0mGVifOgk1Ncb2VJGbVLIdxnp12qqaHO7HXQHURH6ngZ5RVqdCLBBqqj62jCwiknbBJefEd5QCDCCUWgV3hRa+EFFgBEEbXMcBBjeabR55UWLUzYiIMDwRoHVK1iZKoqHAMMLqm49CDAqyxefID42MwCGEWDAMAIMGEaAAcMIMGAYAQYMI8CAYQQYMIwAA4YRYMAwAgwYRoABwwgwYBgBBgwjwIBhBBgwjAADhhFgwDACDBhGgAHDCDBgGAEGDCPAgGEEGDCMAAOGEWDAMAIMGEaAAcMIMGAYAQYMI8CAYQQYMIwAA4YRYMAwAgwYRoABwwgwYBgBBgwjwIBhBBgwjAADhhFgwDACDBhGgAHDCDBgGAEGDCPAgGEEGDCMAAOGEWDAMAIMGEaAAcMIMGAYAQYMI8CAYQQYMIwAA4YRYMAwAgwYRoABwwgwYBgBBgwjwIBhBBgwjAADhv0XZkN9IbEGbp4AAAAASUVORK5CYII=",Zx=({isOpen:n,onClose:i})=>{const[s,l]=Z.useState(""),[c,d]=Z.useState(""),[f,m]=Z.useState(""),[x,y]=Z.useState(null),[S,j]=Z.useState(null),[$,I]=Z.useState(""),{fetchCsrfToken:k}=et(),A=Z.useCallback(()=>{S&&URL.revokeObjectURL(S),j(null),y(null),l(""),d(""),m(""),I("")},[S]),_=Z.useCallback(()=>{A(),i()},[]),J=H=>{var D;const X=(D=H.target.files)==null?void 0:D[0];if(X){y(X);const N=new FileReader;N.onloadend=()=>{j(N.result)},N.readAsDataURL(X)}},G=async H=>{H.preventDefault(),I("");try{const X=new FormData;X.append("userCreateRequest",new Blob([JSON.stringify({email:s,username:c,password:f})],{type:"application/json"})),x&&X.append("profile",x),await bx(X),await k(),i()}catch{I("회원가입에 실패했습니다.")}};return n?h.jsx(Mh,{children:h.jsxs(_h,{children:[h.jsx("h2",{children:"계정 만들기"}),h.jsxs("form",{onSubmit:G,children:[h.jsxs(es,{children:[h.jsxs(ts,{children:["이메일 ",h.jsx(Ua,{children:"*"})]}),h.jsx(To,{type:"email",value:s,onChange:H=>l(H.target.value),required:!0})]}),h.jsxs(es,{children:[h.jsxs(ts,{children:["사용자명 ",h.jsx(Ua,{children:"*"})]}),h.jsx(To,{type:"text",value:c,onChange:H=>d(H.target.value),required:!0})]}),h.jsxs(es,{children:[h.jsxs(ts,{children:["비밀번호 ",h.jsx(Ua,{children:"*"})]}),h.jsx(To,{type:"password",value:f,onChange:H=>m(H.target.value),required:!0})]}),h.jsxs(es,{children:[h.jsx(ts,{children:"프로필 이미지"}),h.jsxs(qx,{children:[h.jsx(Qx,{src:S||Ct,alt:"profile"}),h.jsx(Gx,{type:"file",accept:"image/*",onChange:J,id:"profile-image"}),h.jsx(Kx,{htmlFor:"profile-image",children:"이미지 변경"})]})]}),$&&h.jsx(Nh,{children:$}),h.jsx(Th,{type:"submit",children:"계속하기"}),h.jsx(Jx,{onClick:_,children:"이미 계정이 있으신가요?"})]})]})}):null},ev=({isOpen:n,onClose:i})=>{const[s,l]=Z.useState(""),[c,d]=Z.useState(""),[f,m]=Z.useState(""),[x,y]=Z.useState(!1),{login:S}=et(),{fetchUsers:j}=Nr(),$=Z.useCallback(()=>{l(""),d(""),m(""),y(!1)},[]),I=Z.useCallback(()=>{$(),y(!0)},[$,i]),k=async()=>{var A;try{await S(s,c),await j(),$(),i()}catch(_){console.error("로그인 에러:",_),((A=_.response)==null?void 0:A.status)===401?m("아이디 또는 비밀번호가 올바르지 않습니다."):m("로그인에 실패했습니다.")}};return n?h.jsxs(h.Fragment,{children:[h.jsx(Mh,{children:h.jsxs(_h,{children:[h.jsx("h2",{children:"돌아오신 것을 환영해요!"}),h.jsxs("form",{onSubmit:A=>{A.preventDefault(),k()},children:[h.jsx(To,{type:"text",placeholder:"사용자 이름",value:s,onChange:A=>l(A.target.value)}),h.jsx(To,{type:"password",placeholder:"비밀번호",value:c,onChange:A=>d(A.target.value)}),f&&h.jsx(Nh,{children:f}),h.jsx(Th,{type:"submit",children:"로그인"})]}),h.jsxs(Vx,{children:["계정이 필요한가요? ",h.jsx(Wx,{onClick:I,children:"가입하기"})]})]})}),h.jsx(Zx,{isOpen:x,onClose:()=>y(!1)})]}):null},tv=async n=>(await Le.get(`/channels?userId=${n}`)).data,nv=async n=>(await Le.post("/channels/public",n)).data,rv=async n=>{const i={participantIds:n};return(await Le.post("/channels/private",i)).data},ov=async(n,i)=>(await Le.patch(`/channels/${n}`,i)).data,iv=async n=>{await Le.delete(`/channels/${n}`)},sv=async n=>(await Le.get("/readStatuses",{params:{userId:n}})).data,cp=async(n,{newLastReadAt:i,newNotificationEnabled:s})=>{const l={newLastReadAt:i,newNotificationEnabled:s};return(await Le.patch(`/readStatuses/${n}`,l)).data},lv=async(n,i,s)=>{const l={userId:n,channelId:i,lastReadAt:s};return(await Le.post("/readStatuses",l)).data},Ar=Kn((n,i)=>({readStatuses:{},fetchReadStatuses:async()=>{try{const{currentUser:s}=et.getState();if(!s)return;const c=(await sv(s.id)).reduce((d,f)=>(d[f.channelId]={id:f.id,lastReadAt:f.lastReadAt,notificationEnabled:f.notificationEnabled},d),{});n({readStatuses:c})}catch(s){console.error("읽음 상태 조회 실패:",s)}},updateReadStatus:async s=>{try{const{currentUser:l}=et.getState();if(!l)return;const c=i().readStatuses[s];let d;c?d=await cp(c.id,{newLastReadAt:new Date().toISOString(),newNotificationEnabled:null}):d=await lv(l.id,s,new Date().toISOString()),n(f=>({readStatuses:{...f.readStatuses,[s]:{id:d.id,lastReadAt:d.lastReadAt,notificationEnabled:d.notificationEnabled}}}))}catch(l){console.error("읽음 상태 업데이트 실패:",l)}},updateNotificationEnabled:async(s,l)=>{try{const{currentUser:c}=et.getState();if(!c)return;const d=i().readStatuses[s];let f;if(d)f=await cp(d.id,{newLastReadAt:null,newNotificationEnabled:l});else return;n(m=>({readStatuses:{...m.readStatuses,[s]:{id:f.id,lastReadAt:f.lastReadAt,notificationEnabled:f.notificationEnabled}}}))}catch(c){console.error("알림 상태 업데이트 실패:",c)}},hasUnreadMessages:(s,l)=>{const c=i().readStatuses[s],d=c==null?void 0:c.lastReadAt;return!d||new Date(l)>new Date(d)}})),An=Kn((n,i)=>({channels:[],pollingInterval:null,loading:!1,error:null,fetchChannels:async s=>{n({loading:!0,error:null});try{const l=await tv(s);n(d=>{const f=new Set(d.channels.map(S=>S.id)),m=l.filter(S=>!f.has(S.id));return{channels:[...d.channels.filter(S=>l.some(j=>j.id===S.id)),...m],loading:!1}});const{fetchReadStatuses:c}=Ar.getState();return c(),l}catch(l){return n({error:l,loading:!1}),[]}},startPolling:s=>{const l=i().pollingInterval;l&&clearInterval(l);const c=setInterval(()=>{i().fetchChannels(s)},3e3);n({pollingInterval:c})},stopPolling:()=>{const s=i().pollingInterval;s&&(clearInterval(s),n({pollingInterval:null}))},createPublicChannel:async s=>{try{const l=await nv(s);return n(c=>c.channels.some(f=>f.id===l.id)?c:{channels:[...c.channels,{...l,participantIds:[],lastMessageAt:new Date().toISOString()}]}),l}catch(l){throw console.error("공개 채널 생성 실패:",l),l}},createPrivateChannel:async s=>{try{const l=await rv(s);return n(c=>c.channels.some(f=>f.id===l.id)?c:{channels:[...c.channels,{...l,participantIds:s,lastMessageAt:new Date().toISOString()}]}),l}catch(l){throw console.error("비공개 채널 생성 실패:",l),l}},updatePublicChannel:async(s,l)=>{try{const c=await ov(s,l);return n(d=>({channels:d.channels.map(f=>f.id===s?{...f,...c}:f)})),c}catch(c){throw console.error("채널 수정 실패:",c),c}},deleteChannel:async s=>{try{await iv(s),n(l=>({channels:l.channels.filter(c=>c.id!==s)}))}catch(l){throw console.error("채널 삭제 실패:",l),l}}})),dp=async n=>(await Le.get(`/binaryContents/${n}`)).data,fp=async n=>({blob:(await Le.get(`/binaryContents/${n}/download`,{responseType:"blob"})).data});var jn=(n=>(n.USER="USER",n.CHANNEL_MANAGER="CHANNEL_MANAGER",n.ADMIN="ADMIN",n))(jn||{}),Cr=(n=>(n.PROCESSING="PROCESSING",n.SUCCESS="SUCCESS",n.FAIL="FAIL",n))(Cr||{});let Fn={};const Rn=Kn((n,i)=>({binaryContents:{},pollingIds:new Set,fetchBinaryContent:async s=>{if(i().binaryContents[s])return i().binaryContents[s];try{const l=await dp(s),{contentType:c,fileName:d,size:f,status:m}=l,x={contentType:c,fileName:d,size:f,status:m};if(m===Cr.SUCCESS){const y=await fp(s),S=URL.createObjectURL(y.blob);x.url=S,x.revokeUrl=()=>URL.revokeObjectURL(S)}return n(y=>({binaryContents:{...y.binaryContents,[s]:x}})),x}catch(l){return console.error("첨부파일 정보 조회 실패:",l),null}},startPolling:s=>{if(Fn[s])return;const l=setInterval(async()=>{try{const c=await dp(s),{status:d}=c;if(d===Cr.SUCCESS){console.log(`Polling: ${s} 상태가 SUCCESS로 변경됨`);const f=await fp(s),m=URL.createObjectURL(f.blob);n(x=>({binaryContents:{...x.binaryContents,[s]:{...x.binaryContents[s],url:m,status:Cr.SUCCESS,revokeUrl:()=>URL.revokeObjectURL(m)}}})),i().stopPolling(s)}else d===Cr.FAIL?(console.log(`Polling: ${s} 상태가 FAIL로 변경됨`),n(f=>({binaryContents:{...f.binaryContents,[s]:{...f.binaryContents[s],status:Cr.FAIL}}})),i().stopPolling(s)):console.log(`Polling: ${s} 상태가 여전히 PROCESSING임`)}catch(c){console.error("polling 중 오류:",c),i().stopPolling(s)}},2e3);Fn[s]=l,n(c=>({pollingIds:new Set([...c.pollingIds,s])}))},stopPolling:s=>{Fn[s]&&(clearInterval(Fn[s]),delete Fn[s]),n(l=>{const c=new Set(l.pollingIds);return c.delete(s),{pollingIds:c}})},clearAllPolling:()=>{Object.values(Fn).forEach(s=>{clearInterval(s)}),Fn={},n({pollingIds:new Set})},clearBinaryContent:s=>{const{binaryContents:l}=i(),c=l[s];c!=null&&c.revokeUrl&&(c.revokeUrl(),n(d=>{const{[s]:f,...m}=d.binaryContents;return{binaryContents:m}}))},clearBinaryContents:s=>{const{binaryContents:l}=i(),c=[];s.forEach(d=>{const f=l[d];f&&(f.revokeUrl&&f.revokeUrl(),c.push(d))}),c.length>0&&n(d=>{const f={...d.binaryContents};return c.forEach(m=>{delete f[m]}),{binaryContents:f}})},clearAllBinaryContents:()=>{const{binaryContents:s}=i();Object.values(s).forEach(l=>{l.revokeUrl&&l.revokeUrl()}),n({binaryContents:{}})}})),Fo=E.div`
  position: absolute;
  bottom: -3px;
  right: -3px;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: ${n=>n.$online?ee.colors.status.online:ee.colors.status.offline};
  border: 4px solid ${n=>n.$background||ee.colors.background.secondary};
`;E.div`
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 8px;
  background: ${n=>ee.colors.status[n.status||"offline"]||ee.colors.status.offline};
`;const Dr=E.div`
  position: relative;
  width: ${n=>n.$size||"32px"};
  height: ${n=>n.$size||"32px"};
  flex-shrink: 0;
  margin: ${n=>n.$margin||"0"};
`,rn=E.img`
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
  border: ${n=>n.$border||"none"};
`;function av({isOpen:n,onClose:i,user:s}){var N,Q;const[l,c]=Z.useState(s.username),[d,f]=Z.useState(s.email),[m,x]=Z.useState(""),[y,S]=Z.useState(null),[j,$]=Z.useState(""),[I,k]=Z.useState(null),{binaryContents:A,fetchBinaryContent:_}=Rn(),{logout:J,refreshToken:G}=et();Z.useEffect(()=>{var le;(le=s.profile)!=null&&le.id&&!A[s.profile.id]&&_(s.profile.id)},[s.profile,A,_]);const H=()=>{c(s.username),f(s.email),x(""),S(null),k(null),$(""),i()},X=le=>{var ge;const Se=(ge=le.target.files)==null?void 0:ge[0];if(Se){S(Se);const pe=new FileReader;pe.onloadend=()=>{k(pe.result)},pe.readAsDataURL(Se)}},D=async le=>{le.preventDefault(),$("");try{const Se=new FormData,ge={};l!==s.username&&(ge.newUsername=l),d!==s.email&&(ge.newEmail=d),m&&(ge.newPassword=m),(Object.keys(ge).length>0||y)&&(Se.append("userUpdateRequest",new Blob([JSON.stringify(ge)],{type:"application/json"})),y&&Se.append("profile",y),await Hx(s.id,Se),await G()),i()}catch{$("사용자 정보 수정에 실패했습니다.")}};return n?h.jsx(uv,{children:h.jsxs(cv,{children:[h.jsx("h2",{children:"프로필 수정"}),h.jsxs("form",{onSubmit:D,children:[h.jsxs(ns,{children:[h.jsx(rs,{children:"프로필 이미지"}),h.jsxs(fv,{children:[h.jsx(pv,{src:I||((N=s.profile)!=null&&N.id?(Q=A[s.profile.id])==null?void 0:Q.url:void 0)||Ct,alt:"profile"}),h.jsx(hv,{type:"file",accept:"image/*",onChange:X,id:"profile-image"}),h.jsx(mv,{htmlFor:"profile-image",children:"이미지 변경"})]})]}),h.jsxs(ns,{children:[h.jsxs(rs,{children:["사용자명 ",h.jsx(hp,{children:"*"})]}),h.jsx(Ha,{type:"text",value:l,onChange:le=>c(le.target.value),required:!0})]}),h.jsxs(ns,{children:[h.jsxs(rs,{children:["이메일 ",h.jsx(hp,{children:"*"})]}),h.jsx(Ha,{type:"email",value:d,onChange:le=>f(le.target.value),required:!0})]}),h.jsxs(ns,{children:[h.jsx(rs,{children:"새 비밀번호"}),h.jsx(Ha,{type:"password",placeholder:"변경하지 않으려면 비워두세요",value:m,onChange:le=>x(le.target.value)})]}),j&&h.jsx(dv,{children:j}),h.jsxs(gv,{children:[h.jsx(pp,{type:"button",onClick:H,$secondary:!0,children:"취소"}),h.jsx(pp,{type:"submit",children:"저장"})]})]}),h.jsx(yv,{onClick:J,children:"로그아웃"})]})}):null}const uv=E.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
`,cv=E.div`
  background: ${({theme:n})=>n.colors.background.secondary};
  padding: 32px;
  border-radius: 5px;
  width: 100%;
  max-width: 480px;

  h2 {
    color: ${({theme:n})=>n.colors.text.primary};
    margin-bottom: 24px;
    text-align: center;
    font-size: 24px;
  }
`,Ha=E.input`
  width: 100%;
  padding: 10px;
  margin-bottom: 10px;
  border: none;
  border-radius: 4px;
  background: ${({theme:n})=>n.colors.background.input};
  color: ${({theme:n})=>n.colors.text.primary};

  &::placeholder {
    color: ${({theme:n})=>n.colors.text.muted};
  }

  &:focus {
    outline: none;
    box-shadow: 0 0 0 2px ${({theme:n})=>n.colors.brand.primary};
  }
`,pp=E.button`
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 4px;
  background: ${({$secondary:n,theme:i})=>n?"transparent":i.colors.brand.primary};
  color: ${({theme:n})=>n.colors.text.primary};
  cursor: pointer;
  font-weight: 500;
  
  &:hover {
    background: ${({$secondary:n,theme:i})=>n?i.colors.background.hover:i.colors.brand.hover};
  }
`,dv=E.div`
  color: ${({theme:n})=>n.colors.status.error};
  font-size: 14px;
  margin-bottom: 10px;
`,fv=E.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
`,pv=E.img`
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-bottom: 10px;
  object-fit: cover;
`,hv=E.input`
  display: none;
`,mv=E.label`
  color: ${({theme:n})=>n.colors.brand.primary};
  cursor: pointer;
  font-size: 14px;
  
  &:hover {
    text-decoration: underline;
  }
`,gv=E.div`
  display: flex;
  gap: 10px;
  margin-top: 20px;
`,yv=E.button`
  width: 100%;
  padding: 10px;
  margin-top: 16px;
  border: none;
  border-radius: 4px;
  background: transparent;
  color: ${({theme:n})=>n.colors.status.error};
  cursor: pointer;
  font-weight: 500;
  
  &:hover {
    background: ${({theme:n})=>n.colors.status.error}20;
  }
`,ns=E.div`
  margin-bottom: 20px;
`,rs=E.label`
  display: block;
  color: ${({theme:n})=>n.colors.text.muted};
  font-size: 12px;
  font-weight: 700;
  margin-bottom: 8px;
`,hp=E.span`
  color: ${({theme:n})=>n.colors.status.error};
`,xv=E.div`
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 0.75rem;
  background-color: ${({theme:n})=>n.colors.background.tertiary};
  width: 100%;
  height: 52px;
`,vv=E(Dr)``;E(rn)``;const wv=E.div`
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
`,Sv=E.div`
  font-weight: 500;
  color: ${({theme:n})=>n.colors.text.primary};
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-size: 0.875rem;
  line-height: 1.2;
`,kv=E.div`
  font-size: 0.75rem;
  color: ${({theme:n})=>n.colors.text.secondary};
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  line-height: 1.2;
`,Cv=E.div`
  display: flex;
  align-items: center;
  flex-shrink: 0;
`,Ev=E.button`
  background: none;
  border: none;
  padding: 0.25rem;
  cursor: pointer;
  color: ${({theme:n})=>n.colors.text.secondary};
  font-size: 18px;
  
  &:hover {
    color: ${({theme:n})=>n.colors.text.primary};
  }
`;function jv({user:n}){var d,f;const[i,s]=Z.useState(!1),{binaryContents:l,fetchBinaryContent:c}=Rn();return Z.useEffect(()=>{var m;(m=n.profile)!=null&&m.id&&!l[n.profile.id]&&c(n.profile.id)},[n.profile,l,c]),h.jsxs(h.Fragment,{children:[h.jsxs(xv,{children:[h.jsxs(vv,{children:[h.jsx(rn,{src:(d=n.profile)!=null&&d.id?(f=l[n.profile.id])==null?void 0:f.url:Ct,alt:n.username}),h.jsx(Fo,{$online:!0})]}),h.jsxs(wv,{children:[h.jsx(Sv,{children:n.username}),h.jsx(kv,{children:"온라인"})]}),h.jsx(Cv,{children:h.jsx(Ev,{onClick:()=>s(!0),children:"⚙️"})})]}),h.jsx(av,{isOpen:i,onClose:()=>s(!1),user:n})]})}const Av=E.div`
  width: 240px;
  background: ${ee.colors.background.secondary};
  border-right: 1px solid ${ee.colors.border.primary};
  display: flex;
  flex-direction: column;
`,Rv=E.div`
  flex: 1;
  overflow-y: auto;
`,Pv=E.div`
  padding: 16px;
  font-size: 16px;
  font-weight: bold;
  color: ${ee.colors.text.primary};
`,Au=E.div`
  height: 34px;
  padding: 0 8px;
  margin: 1px 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  color: ${n=>n.$hasUnread?n.theme.colors.text.primary:n.theme.colors.text.muted};
  font-weight: ${n=>n.$hasUnread?"600":"normal"};
  cursor: pointer;
  background: ${n=>n.$isActive?n.theme.colors.background.hover:"transparent"};
  border-radius: 4px;
  
  &:hover {
    background: ${n=>n.theme.colors.background.hover};
    color: ${n=>n.theme.colors.text.primary};
  }
`,mp=E.div`
  margin-bottom: 8px;
`,fu=E.div`
  padding: 8px 16px;
  display: flex;
  align-items: center;
  color: ${ee.colors.text.muted};
  text-transform: uppercase;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  user-select: none;

  & > span:nth-child(2) {
    flex: 1;
    margin-right: auto;
  }

  &:hover {
    color: ${ee.colors.text.primary};
  }
`,gp=E.span`
  margin-right: 4px;
  font-size: 10px;
  transition: transform 0.2s;
  transform: rotate(${n=>n.$folded?"-90deg":"0deg"});
`,yp=E.div`
  display: ${n=>n.$folded?"none":"block"};
`,pu=E(Au)`
  height: ${n=>n.hasSubtext?"42px":"34px"};
`,Mv=E(Dr)`
  width: 32px;
  height: 32px;
  margin: 0 8px;
`,xp=E.div`
  font-size: 16px;
  line-height: 18px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: ${n=>n.$isActive||n.$hasUnread?n.theme.colors.text.primary:n.theme.colors.text.muted};
  font-weight: ${n=>n.$hasUnread?"600":"normal"};
`;E(Fo)`
  border-color: ${ee.colors.background.primary};
`;const vp=E.button`
  background: none;
  border: none;
  color: ${ee.colors.text.muted};
  font-size: 18px;
  padding: 0;
  cursor: pointer;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s, color 0.2s;

  ${fu}:hover & {
    opacity: 1;
  }

  &:hover {
    color: ${ee.colors.text.primary};
  }
`,_v=E(Dr)`
  width: 40px;
  height: 24px;
  margin: 0 8px;
`,Tv=E.div`
  font-size: 12px;
  line-height: 13px;
  color: ${ee.colors.text.muted};
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`,wp=E.div`
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 2px;
`,$h=E.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
`,Oh=E.div`
  background: ${ee.colors.background.primary};
  border-radius: 4px;
  width: 440px;
  max-width: 90%;
`,Lh=E.div`
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
`,Dh=E.h2`
  color: ${ee.colors.text.primary};
  font-size: 20px;
  font-weight: 600;
  margin: 0;
`,Ih=E.div`
  padding: 0 16px 16px;
`,bh=E.form`
  display: flex;
  flex-direction: column;
  gap: 16px;
`,No=E.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
`,$o=E.label`
  color: ${ee.colors.text.primary};
  font-size: 12px;
  font-weight: 600;
  text-transform: uppercase;
`,zh=E.p`
  color: ${ee.colors.text.muted};
  font-size: 14px;
  margin: -4px 0 0;
`,Io=E.input`
  padding: 10px;
  background: ${ee.colors.background.tertiary};
  border: none;
  border-radius: 3px;
  color: ${ee.colors.text.primary};
  font-size: 16px;

  &:focus {
    outline: none;
    box-shadow: 0 0 0 2px ${ee.colors.status.online};
  }

  &::placeholder {
    color: ${ee.colors.text.muted};
  }
`,Bh=E.button`
  margin-top: 8px;
  padding: 12px;
  background: ${ee.colors.status.online};
  color: white;
  border: none;
  border-radius: 3px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #3ca374;
  }
`,Fh=E.button`
  background: none;
  border: none;
  color: ${ee.colors.text.muted};
  font-size: 24px;
  cursor: pointer;
  padding: 4px;
  line-height: 1;

  &:hover {
    color: ${ee.colors.text.primary};
  }
`,Nv=E(Io)`
  margin-bottom: 8px;
`,$v=E.div`
  max-height: 300px;
  overflow-y: auto;
  background: ${ee.colors.background.tertiary};
  border-radius: 4px;
`,Ov=E.div`
  display: flex;
  align-items: center;
  padding: 8px 12px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: ${ee.colors.background.hover};
  }

  & + & {
    border-top: 1px solid ${ee.colors.border.primary};
  }
`,Lv=E.input`
  margin-right: 12px;
  width: 16px;
  height: 16px;
  cursor: pointer;
`,Sp=E.img`
  width: 32px;
  height: 32px;
  border-radius: 50%;
  margin-right: 12px;
`,Dv=E.div`
  flex: 1;
  min-width: 0;
`,Iv=E.div`
  color: ${ee.colors.text.primary};
  font-size: 14px;
  font-weight: 500;
`,bv=E.div`
  color: ${ee.colors.text.muted};
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`,zv=E.div`
  padding: 16px;
  text-align: center;
  color: ${ee.colors.text.muted};
`,Uh=E.div`
  color: ${ee.colors.status.error};
  font-size: 14px;
  padding: 8px 0;
  text-align: center;
  background-color: ${({theme:n})=>n.colors.background.tertiary};
  border-radius: 4px;
  margin-bottom: 8px;
`,Ya=E.div`
  position: relative;
  margin-left: auto;
  z-index: 99999;
`,Va=E.button`
  background: none;
  border: none;
  color: ${({theme:n})=>n.colors.text.muted};
  font-size: 16px;
  cursor: pointer;
  padding: 4px;
  border-radius: 3px;
  opacity: 0;
  transition: opacity 0.2s, background 0.2s;

  &:hover {
    background: ${({theme:n})=>n.colors.background.hover};
    color: ${({theme:n})=>n.colors.text.primary};
  }

  ${Au}:hover &,
  ${pu}:hover & {
    opacity: 1;
  }
`,Wa=E.div`
  position: absolute;
  top: 100%;
  right: 0;
  background: ${({theme:n})=>n.colors.background.primary};
  border: 1px solid ${({theme:n})=>n.colors.border.primary};
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  min-width: 120px;
  z-index: 100000;
`,os=E.div`
  padding: 8px 12px;
  color: ${({theme:n})=>n.colors.text.primary};
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;

  &:hover {
    background: ${({theme:n})=>n.colors.background.hover};
  }

  &:first-child {
    border-radius: 4px 4px 0 0;
  }

  &:last-child {
    border-radius: 0 0 4px 4px;
  }

  &:only-child {
    border-radius: 4px;
  }
`;function Bv(){return h.jsx(Pv,{children:"채널 목록"})}function Fv({isOpen:n,channel:i,onClose:s,onUpdateSuccess:l}){const[c,d]=Z.useState({name:"",description:""}),[f,m]=Z.useState(""),[x,y]=Z.useState(!1),{updatePublicChannel:S}=An();Z.useEffect(()=>{i&&n&&(d({name:i.name||"",description:i.description||""}),m(""))},[i,n]);const j=I=>{const{name:k,value:A}=I.target;d(_=>({..._,[k]:A}))},$=async I=>{var k,A;if(I.preventDefault(),!!i){m(""),y(!0);try{if(!c.name.trim()){m("채널 이름을 입력해주세요."),y(!1);return}const _={newName:c.name.trim(),newDescription:c.description.trim()},J=await S(i.id,_);l(J)}catch(_){console.error("채널 수정 실패:",_),m(((A=(k=_.response)==null?void 0:k.data)==null?void 0:A.message)||"채널 수정에 실패했습니다. 다시 시도해주세요.")}finally{y(!1)}}};return!n||!i||i.type!=="PUBLIC"?null:h.jsx($h,{onClick:s,children:h.jsxs(Oh,{onClick:I=>I.stopPropagation(),children:[h.jsxs(Lh,{children:[h.jsx(Dh,{children:"채널 수정"}),h.jsx(Fh,{onClick:s,children:"×"})]}),h.jsx(Ih,{children:h.jsxs(bh,{onSubmit:$,children:[f&&h.jsx(Uh,{children:f}),h.jsxs(No,{children:[h.jsx($o,{children:"채널 이름"}),h.jsx(Io,{name:"name",value:c.name,onChange:j,placeholder:"새로운-채널",required:!0,disabled:x})]}),h.jsxs(No,{children:[h.jsx($o,{children:"채널 설명"}),h.jsx(zh,{children:"이 채널의 주제를 설명해주세요."}),h.jsx(Io,{name:"description",value:c.description,onChange:j,placeholder:"채널 설명을 입력하세요",disabled:x})]}),h.jsx(Bh,{type:"submit",disabled:x,children:x?"수정 중...":"채널 수정"})]})})]})})}function kp({channel:n,isActive:i,onClick:s,hasUnread:l}){var G;const{currentUser:c}=et(),{binaryContents:d}=Rn(),{deleteChannel:f}=An(),[m,x]=Z.useState(null),[y,S]=Z.useState(!1),j=(c==null?void 0:c.role)===jn.ADMIN||(c==null?void 0:c.role)===jn.CHANNEL_MANAGER;Z.useEffect(()=>{const H=()=>{m&&x(null)};if(m)return document.addEventListener("click",H),()=>document.removeEventListener("click",H)},[m]);const $=H=>{x(m===H?null:H)},I=()=>{x(null),S(!0)},k=H=>{S(!1),console.log("Channel updated successfully:",H)},A=()=>{S(!1)},_=async H=>{var D;x(null);const X=n.type==="PUBLIC"?n.name:n.type==="PRIVATE"&&n.participants.length>2?`그룹 채팅 (멤버 ${n.participants.length}명)`:((D=n.participants.filter(N=>N.id!==(c==null?void 0:c.id))[0])==null?void 0:D.username)||"1:1 채팅";if(confirm(`"${X}" 채널을 삭제하시겠습니까?`))try{await f(H),console.log("Channel deleted successfully:",H)}catch(N){console.error("Channel delete failed:",N),alert("채널 삭제에 실패했습니다. 다시 시도해주세요.")}};let J;if(n.type==="PUBLIC")J=h.jsxs(Au,{$isActive:i,onClick:s,$hasUnread:l,children:["# ",n.name,j&&h.jsxs(Ya,{children:[h.jsx(Va,{onClick:H=>{H.stopPropagation(),$(n.id)},children:"⋯"}),m===n.id&&h.jsxs(Wa,{onClick:H=>H.stopPropagation(),children:[h.jsx(os,{onClick:()=>I(),children:"✏️ 수정"}),h.jsx(os,{onClick:()=>_(n.id),children:"🗑️ 삭제"})]})]})]});else{const H=n.participants;if(H.length>2){const X=H.filter(D=>D.id!==(c==null?void 0:c.id)).map(D=>D.username).join(", ");J=h.jsxs(pu,{$isActive:i,onClick:s,children:[h.jsx(_v,{children:H.filter(D=>D.id!==(c==null?void 0:c.id)).slice(0,2).map((D,N)=>{var Q;return h.jsx(rn,{src:D.profile?(Q=d[D.profile.id])==null?void 0:Q.url:Ct,style:{position:"absolute",left:N*16,zIndex:2-N,width:"24px",height:"24px",border:"2px solid #2a2a2a"}},D.id)})}),h.jsxs(wp,{children:[h.jsx(xp,{$hasUnread:l,children:X}),h.jsxs(Tv,{children:["멤버 ",H.length,"명"]})]}),j&&h.jsxs(Ya,{children:[h.jsx(Va,{onClick:D=>{D.stopPropagation(),$(n.id)},children:"⋯"}),m===n.id&&h.jsx(Wa,{onClick:D=>D.stopPropagation(),children:h.jsx(os,{onClick:()=>_(n.id),children:"🗑️ 삭제"})})]})]})}else{const X=H.filter(D=>D.id!==(c==null?void 0:c.id))[0];J=X?h.jsxs(pu,{$isActive:i,onClick:s,children:[h.jsxs(Mv,{children:[h.jsx(rn,{src:X.profile?(G=d[X.profile.id])==null?void 0:G.url:Ct,alt:"profile"}),h.jsx(Fo,{$online:X.online})]}),h.jsx(wp,{children:h.jsx(xp,{$hasUnread:l,children:X.username})}),j&&h.jsxs(Ya,{children:[h.jsx(Va,{onClick:D=>{D.stopPropagation(),$(n.id)},children:"⋯"}),m===n.id&&h.jsx(Wa,{onClick:D=>D.stopPropagation(),children:h.jsx(os,{onClick:()=>_(n.id),children:"🗑️ 삭제"})})]})]}):h.jsx("div",{})}}return h.jsxs(h.Fragment,{children:[J,h.jsx(Fv,{isOpen:y,channel:n,onClose:A,onUpdateSuccess:k})]})}function Uv({isOpen:n,type:i,onClose:s,onCreateSuccess:l}){const[c,d]=Z.useState({name:"",description:""}),[f,m]=Z.useState(""),[x,y]=Z.useState([]),[S,j]=Z.useState(""),$=Nr(D=>D.users),I=Rn(D=>D.binaryContents),{currentUser:k}=et(),A=Z.useMemo(()=>$.filter(D=>D.id!==(k==null?void 0:k.id)).filter(D=>D.username.toLowerCase().includes(f.toLowerCase())||D.email.toLowerCase().includes(f.toLowerCase())),[f,$,k]),_=An(D=>D.createPublicChannel),J=An(D=>D.createPrivateChannel),G=D=>{const{name:N,value:Q}=D.target;d(le=>({...le,[N]:Q}))},H=D=>{y(N=>N.includes(D)?N.filter(Q=>Q!==D):[...N,D])},X=async D=>{var N,Q;D.preventDefault(),j("");try{let le;if(i==="PUBLIC"){if(!c.name.trim()){j("채널 이름을 입력해주세요.");return}const Se={name:c.name,description:c.description};le=await _(Se)}else{if(x.length===0){j("대화 상대를 선택해주세요.");return}const Se=(k==null?void 0:k.id)&&[...x,k.id]||x;le=await J(Se)}l(le)}catch(le){console.error("채널 생성 실패:",le),j(((Q=(N=le.response)==null?void 0:N.data)==null?void 0:Q.message)||"채널 생성에 실패했습니다. 다시 시도해주세요.")}};return n?h.jsx($h,{onClick:s,children:h.jsxs(Oh,{onClick:D=>D.stopPropagation(),children:[h.jsxs(Lh,{children:[h.jsx(Dh,{children:i==="PUBLIC"?"채널 만들기":"개인 메시지 시작하기"}),h.jsx(Fh,{onClick:s,children:"×"})]}),h.jsx(Ih,{children:h.jsxs(bh,{onSubmit:X,children:[S&&h.jsx(Uh,{children:S}),i==="PUBLIC"?h.jsxs(h.Fragment,{children:[h.jsxs(No,{children:[h.jsx($o,{children:"채널 이름"}),h.jsx(Io,{name:"name",value:c.name,onChange:G,placeholder:"새로운-채널",required:!0})]}),h.jsxs(No,{children:[h.jsx($o,{children:"채널 설명"}),h.jsx(zh,{children:"이 채널의 주제를 설명해주세요."}),h.jsx(Io,{name:"description",value:c.description,onChange:G,placeholder:"채널 설명을 입력하세요"})]})]}):h.jsxs(No,{children:[h.jsx($o,{children:"사용자 검색"}),h.jsx(Nv,{type:"text",value:f,onChange:D=>m(D.target.value),placeholder:"사용자명 또는 이메일로 검색"}),h.jsx($v,{children:A.length>0?A.map(D=>h.jsxs(Ov,{children:[h.jsx(Lv,{type:"checkbox",checked:x.includes(D.id),onChange:()=>H(D.id)}),D.profile?h.jsx(Sp,{src:I[D.profile.id].url}):h.jsx(Sp,{src:Ct}),h.jsxs(Dv,{children:[h.jsx(Iv,{children:D.username}),h.jsx(bv,{children:D.email})]})]},D.id)):h.jsx(zv,{children:"검색 결과가 없습니다."})})]}),h.jsx(Bh,{type:"submit",children:i==="PUBLIC"?"채널 만들기":"대화 시작하기"})]})})]})}):null}function Hv({currentUser:n,activeChannel:i,onChannelSelect:s}){var X,D;const[l,c]=Z.useState({PUBLIC:!1,PRIVATE:!1}),[d,f]=Z.useState({isOpen:!1,type:null}),m=An(N=>N.channels),x=An(N=>N.fetchChannels),y=An(N=>N.startPolling),S=An(N=>N.stopPolling),j=Ar(N=>N.fetchReadStatuses),$=Ar(N=>N.updateReadStatus),I=Ar(N=>N.hasUnreadMessages);Z.useEffect(()=>{if(n)return x(n.id),j(),y(n.id),()=>{S()}},[n,x,j,y,S]);const k=N=>{c(Q=>({...Q,[N]:!Q[N]}))},A=(N,Q)=>{Q.stopPropagation(),f({isOpen:!0,type:N})},_=()=>{f({isOpen:!1,type:null})},J=async N=>{try{const le=(await x(n.id)).find(Se=>Se.id===N.id);le&&s(le),_()}catch(Q){console.error("채널 생성 실패:",Q)}},G=N=>{s(N),$(N.id)},H=m.reduce((N,Q)=>(N[Q.type]||(N[Q.type]=[]),N[Q.type].push(Q),N),{});return h.jsxs(Av,{children:[h.jsx(Bv,{}),h.jsxs(Rv,{children:[h.jsxs(mp,{children:[h.jsxs(fu,{onClick:()=>k("PUBLIC"),children:[h.jsx(gp,{$folded:l.PUBLIC,children:"▼"}),h.jsx("span",{children:"일반 채널"}),h.jsx(vp,{onClick:N=>A("PUBLIC",N),children:"+"})]}),h.jsx(yp,{$folded:l.PUBLIC,children:(X=H.PUBLIC)==null?void 0:X.map(N=>h.jsx(kp,{channel:N,isActive:(i==null?void 0:i.id)===N.id,hasUnread:I(N.id,N.lastMessageAt),onClick:()=>G(N)},N.id))})]}),h.jsxs(mp,{children:[h.jsxs(fu,{onClick:()=>k("PRIVATE"),children:[h.jsx(gp,{$folded:l.PRIVATE,children:"▼"}),h.jsx("span",{children:"개인 메시지"}),h.jsx(vp,{onClick:N=>A("PRIVATE",N),children:"+"})]}),h.jsx(yp,{$folded:l.PRIVATE,children:(D=H.PRIVATE)==null?void 0:D.map(N=>h.jsx(kp,{channel:N,isActive:(i==null?void 0:i.id)===N.id,hasUnread:I(N.id,N.lastMessageAt),onClick:()=>G(N)},N.id))})]})]}),h.jsx(Yv,{children:h.jsx(jv,{user:n})}),h.jsx(Uv,{isOpen:d.isOpen,type:d.type,onClose:_,onCreateSuccess:J})]})}const Yv=E.div`
  margin-top: auto;
  border-top: 1px solid ${({theme:n})=>n.colors.border.primary};
  background-color: ${({theme:n})=>n.colors.background.tertiary};
`,Vv=E.div`
  flex: 1;
  display: flex;
  flex-direction: column;
  background: ${({theme:n})=>n.colors.background.primary};
`,Wv=E.div`
  display: flex;
  flex-direction: column;
  height: 100%;
  background: ${({theme:n})=>n.colors.background.primary};
`,qv=E(Wv)`
  justify-content: center;
  align-items: center;
  flex: 1;
  padding: 0 20px;
`,Qv=E.div`
  text-align: center;
  max-width: 400px;
  padding: 20px;
  margin-bottom: 80px;
`,Gv=E.div`
  font-size: 48px;
  margin-bottom: 16px;
  animation: wave 2s infinite;
  transform-origin: 70% 70%;

  @keyframes wave {
    0% { transform: rotate(0deg); }
    10% { transform: rotate(14deg); }
    20% { transform: rotate(-8deg); }
    30% { transform: rotate(14deg); }
    40% { transform: rotate(-4deg); }
    50% { transform: rotate(10deg); }
    60% { transform: rotate(0deg); }
    100% { transform: rotate(0deg); }
  }
`,Kv=E.h2`
  color: ${({theme:n})=>n.colors.text.primary};
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 16px;
`,Xv=E.p`
  color: ${({theme:n})=>n.colors.text.muted};
  font-size: 16px;
  line-height: 1.6;
  word-break: keep-all;
`,Cp=E.div`
  height: 48px;
  padding: 0 16px;
  background: ${ee.colors.background.primary};
  border-bottom: 1px solid ${ee.colors.border.primary};
  display: flex;
  align-items: center;
`,Ep=E.div`
  display: flex;
  align-items: center;
  gap: 8px;
  height: 100%;
`,Jv=E.div`
  display: flex;
  align-items: center;
  gap: 12px;
  height: 100%;
`,Zv=E(Dr)`
  width: 24px;
  height: 24px;
`;E.img`
  width: 24px;
  height: 24px;
  border-radius: 50%;
`;const e1=E.div`
  position: relative;
  width: 40px;
  height: 24px;
  flex-shrink: 0;
`,t1=E(Fo)`
  border-color: ${ee.colors.background.primary};
  bottom: -3px;
  right: -3px;
`,n1=E.div`
  font-size: 12px;
  color: ${ee.colors.text.muted};
  line-height: 13px;
`,jp=E.div`
  font-weight: bold;
  color: ${ee.colors.text.primary};
  line-height: 20px;
  font-size: 16px;
`,r1=E.div`
  flex: 1;
  display: flex;
  flex-direction: column-reverse;
  overflow-y: auto;
  position: relative;
`,o1=E.div`
  padding: 16px;
  display: flex;
  flex-direction: column;
`,Hh=E.div`
  margin-bottom: 16px;
  display: flex;
  align-items: flex-start;
  position: relative;
  z-index: 1;
`,i1=E(Dr)`
  margin-right: 16px;
  width: 40px;
  height: 40px;
`;E.img`
  width: 40px;
  height: 40px;
  border-radius: 50%;
`;const s1=E.div`
  display: flex;
  align-items: center;
  margin-bottom: 4px;
  position: relative;
`,l1=E.span`
  font-weight: bold;
  color: ${ee.colors.text.primary};
  margin-right: 8px;
`,a1=E.span`
  font-size: 0.75rem;
  color: ${ee.colors.text.muted};
`,u1=E.div`
  color: ${ee.colors.text.secondary};
  margin-top: 4px;
`,c1=E.form`
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px;
  background: ${({theme:n})=>n.colors.background.secondary};
  position: relative;
  z-index: 1;
`,d1=E.textarea`
  flex: 1;
  padding: 12px;
  background: ${({theme:n})=>n.colors.background.tertiary};
  border: none;
  border-radius: 4px;
  color: ${({theme:n})=>n.colors.text.primary};
  font-size: 14px;
  resize: none;
  min-height: 44px;
  max-height: 144px;

  &:focus {
    outline: none;
  }

  &::placeholder {
    color: ${({theme:n})=>n.colors.text.muted};
  }
`,f1=E.button`
  background: none;
  border: none;
  color: ${({theme:n})=>n.colors.text.muted};
  font-size: 24px;
  cursor: pointer;
  padding: 4px 8px;
  display: flex;
  align-items: center;
  justify-content: center;

  &:hover {
    color: ${({theme:n})=>n.colors.text.primary};
  }
`;E.div`
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: ${ee.colors.text.muted};
  font-size: 16px;
  font-weight: 500;
  padding: 20px;
  text-align: center;
`;const is=E.div`
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
  width: 100%;
`,p1=E.a`
  display: block;
  border-radius: 4px;
  overflow: hidden;
  max-width: 300px;
  
  img {
    width: 100%;
    height: auto;
    display: block;
  }
`,qa=E.a`
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: ${({theme:n})=>n.colors.background.tertiary};
  border-radius: 8px;
  text-decoration: none;
  width: fit-content;

  &:hover {
    background: ${({theme:n})=>n.colors.background.hover};
  }
`,Qa=E.div`
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  color: #0B93F6;
`,Ga=E.div`
  display: flex;
  flex-direction: column;
  gap: 2px;
`,Ka=E.span`
  font-size: 14px;
  color: #0B93F6;
  font-weight: 500;
`,Xa=E.span`
  font-size: 13px;
  color: ${({theme:n})=>n.colors.text.muted};
`,h1=E.div`
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 8px 0;
`,Yh=E.div`
  position: relative;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: ${({theme:n})=>n.colors.background.tertiary};
  border-radius: 4px;
  max-width: 300px;
`,m1=E(Yh)`
  padding: 0;
  overflow: hidden;
  width: 200px;
  height: 120px;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
`,g1=E.div`
  color: #0B93F6;
  font-size: 20px;
`,y1=E.div`
  font-size: 13px;
  color: ${({theme:n})=>n.colors.text.primary};
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
`,Ap=E.button`
  position: absolute;
  top: -6px;
  right: -6px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: ${({theme:n})=>n.colors.background.secondary};
  border: none;
  color: ${({theme:n})=>n.colors.text.muted};
  font-size: 16px;
  line-height: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

  &:hover {
    color: ${({theme:n})=>n.colors.text.primary};
  }
`,x1=E.div`
  width: 16px;
  height: 16px;
  border: 2px solid ${({theme:n})=>n.colors.background.tertiary};
  border-top: 2px solid ${({theme:n})=>n.colors.brand.primary};
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-right: 8px;
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`,v1=E.div`
  position: relative;
  margin-left: auto;
  z-index: 99999;
`,w1=E.button`
  background: none;
  border: none;
  color: ${({theme:n})=>n.colors.text.muted};
  font-size: 16px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease;

  &:hover {
    color: ${({theme:n})=>n.colors.text.primary};
    background: ${({theme:n})=>n.colors.background.hover};
  }

  ${Hh}:hover & {
    opacity: 1;
  }
`,S1=E.div`
  position: absolute;
  top: 0;
  background: ${({theme:n})=>n.colors.background.primary};
  border: 1px solid ${({theme:n})=>n.colors.border.primary};
  border-radius: 6px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
  width: 80px;
  z-index: 99999;
  overflow: hidden;
`,Rp=E.button`
  display: flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  background: none;
  border: none;
  color: ${({theme:n})=>n.colors.text.primary};
  font-size: 14px;
  cursor: pointer;
  text-align: center ;

  &:hover {
    background: ${({theme:n})=>n.colors.background.hover};
  }

  &:first-child {
    border-radius: 6px 6px 0 0;
  }

  &:last-child {
    border-radius: 0 0 6px 6px;
  }
`,k1=E.div`
  margin-top: 4px;
`,C1=E.textarea`
  width: 100%;
  max-width: 600px;
  min-height: 80px;
  padding: 12px 16px;
  background: ${({theme:n})=>n.colors.background.tertiary};
  border: 1px solid ${({theme:n})=>n.colors.border.primary};
  border-radius: 4px;
  color: ${({theme:n})=>n.colors.text.primary};
  font-size: 14px;
  font-family: inherit;
  resize: vertical;
  outline: none;
  box-sizing: border-box;

  &:focus {
    border-color: ${({theme:n})=>n.colors.primary};
  }

  &::placeholder {
    color: ${({theme:n})=>n.colors.text.muted};
  }
`,E1=E.div`
  display: flex;
  gap: 8px;
  margin-top: 8px;
`,Pp=E.button`
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s ease;

  ${({variant:n,theme:i})=>n==="primary"?`
        background: ${i.colors.primary};
        color: white;
        
        &:hover {
          background: ${i.colors.primaryHover||i.colors.primary};
        }
      `:`
      background: ${i.colors.background.secondary};
      color: ${i.colors.text.secondary};
      
      &:hover {
        background: ${i.colors.background.hover};
      }
    `}
`,Mp=E.button`
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: ${({theme:n,$enabled:i})=>i?n.colors.brand.primary:n.colors.text.muted};
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &:hover {
    background: ${({theme:n})=>n.colors.background.hover};
    color: ${({theme:n})=>n.colors.brand.primary};
  }
`;function j1({channel:n}){var I;const{currentUser:i}=et(),s=Nr(k=>k.users),l=Rn(k=>k.binaryContents),{readStatuses:c,updateNotificationEnabled:d}=Ar(),[f,m]=Z.useState(!1);Z.useEffect(()=>{c[n==null?void 0:n.id]&&m(c[n.id].notificationEnabled)},[c,n]);const x=Z.useCallback(async()=>{if(!i||!n)return;const k=!f;m(k);try{await d(n.id,k)}catch(A){console.error("알림 설정 업데이트 실패:",A),m(f)}},[i,n,f,d]);if(!n)return null;if(n.type==="PUBLIC")return h.jsxs(Cp,{children:[h.jsx(Ep,{children:h.jsxs(jp,{children:["# ",n.name]})}),h.jsx(Mp,{onClick:x,$enabled:f,children:h.jsxs("svg",{width:"20",height:"20",viewBox:"0 0 24 24",fill:"none",stroke:"currentColor",strokeWidth:"2",strokeLinecap:"round",strokeLinejoin:"round",children:[h.jsx("path",{d:"M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"}),h.jsx("path",{d:"M13.73 21a2 2 0 0 1-3.46 0"})]})})]});const y=n.participants.map(k=>s.find(A=>A.id===k.id)).filter(Boolean),S=y.filter(k=>k.id!==(i==null?void 0:i.id)),j=y.length>2,$=y.filter(k=>k.id!==(i==null?void 0:i.id)).map(k=>k.username).join(", ");return h.jsxs(Cp,{children:[h.jsx(Ep,{children:h.jsxs(Jv,{children:[j?h.jsx(e1,{children:S.slice(0,2).map((k,A)=>{var _;return h.jsx(rn,{src:k.profile?(_=l[k.profile.id])==null?void 0:_.url:Ct,style:{position:"absolute",left:A*16,zIndex:2-A,width:"24px",height:"24px"}},k.id)})}):h.jsxs(Zv,{children:[h.jsx(rn,{src:S[0].profile?(I=l[S[0].profile.id])==null?void 0:I.url:Ct}),h.jsx(t1,{$online:S[0].online})]}),h.jsxs("div",{children:[h.jsx(jp,{children:$}),j&&h.jsxs(n1,{children:["멤버 ",y.length,"명"]})]})]})}),h.jsx(Mp,{onClick:x,$enabled:f,children:h.jsxs("svg",{width:"20",height:"20",viewBox:"0 0 24 24",fill:"none",stroke:"currentColor",strokeWidth:"2",strokeLinecap:"round",strokeLinejoin:"round",children:[h.jsx("path",{d:"M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"}),h.jsx("path",{d:"M13.73 21a2 2 0 0 1-3.46 0"})]})})]})}const A1=async(n,i,s)=>{var c;return(await Le.get("/messages",{params:{channelId:n,cursor:i,size:s.size,sort:(c=s.sort)==null?void 0:c.join(",")}})).data},R1=async(n,i)=>{const s=new FormData,l={content:n.content,channelId:n.channelId,authorId:n.authorId};return s.append("messageCreateRequest",new Blob([JSON.stringify(l)],{type:"application/json"})),i&&i.length>0&&i.forEach(d=>{s.append("attachments",d)}),(await Le.post("/messages",s,{headers:{"Content-Type":"multipart/form-data"}})).data},P1=async(n,i)=>(await Le.patch(`/messages/${n}`,i)).data,M1=async n=>{await Le.delete(`/messages/${n}`)},Ja={size:50,sort:["createdAt,desc"]},Vh=Kn((n,i)=>({messages:[],pollingIntervals:{},lastMessageId:null,pagination:{nextCursor:null,pageSize:50,hasNext:!1},isCreating:!1,fetchMessages:async(s,l,c=Ja)=>{try{if(i().isCreating)return Promise.resolve(!0);const d=await A1(s,l,c),f=d.content,m=f.length>0?f[0]:null,x=(m==null?void 0:m.id)!==i().lastMessageId;return n(y=>{var A;const S=!l,j=s!==((A=y.messages[0])==null?void 0:A.channelId),$=S&&(y.messages.length===0||j);let I=[],k={...y.pagination};if($)I=f,k={nextCursor:d.nextCursor,pageSize:d.size,hasNext:d.hasNext};else if(S){const _=new Set(y.messages.map(G=>G.id));I=[...f.filter(G=>!_.has(G.id)&&(y.messages.length===0||G.createdAt>y.messages[0].createdAt)),...y.messages]}else{const _=new Set(y.messages.map(G=>G.id)),J=f.filter(G=>!_.has(G.id));I=[...y.messages,...J],k={nextCursor:d.nextCursor,pageSize:d.size,hasNext:d.hasNext}}return{messages:I,lastMessageId:(m==null?void 0:m.id)||null,pagination:k}}),x}catch(d){return console.error("메시지 목록 조회 실패:",d),!1}},loadMoreMessages:async s=>{const{pagination:l}=i();l.hasNext&&await i().fetchMessages(s,l.nextCursor,{...Ja})},startPolling:s=>{const l=i();if(l.pollingIntervals[s]){const m=l.pollingIntervals[s];typeof m=="number"&&clearTimeout(m)}let c=300;const d=3e3;n(m=>({pollingIntervals:{...m.pollingIntervals,[s]:!0}}));const f=async()=>{const m=i();if(!m.pollingIntervals[s])return;const x=await m.fetchMessages(s,null,Ja);if(!(i().messages.length==0)&&x?c=300:c=Math.min(c*1.5,d),i().pollingIntervals[s]){const S=setTimeout(f,c);n(j=>({pollingIntervals:{...j.pollingIntervals,[s]:S}}))}};f()},stopPolling:s=>{const{pollingIntervals:l}=i();if(l[s]){const c=l[s];typeof c=="number"&&clearTimeout(c),n(d=>{const f={...d.pollingIntervals};return delete f[s],{pollingIntervals:f}})}},createMessage:async(s,l)=>{try{n({isCreating:!0});const c=await R1(s,l),d=Ar.getState().updateReadStatus;return await d(s.channelId),n(f=>f.messages.some(x=>x.id===c.id)?f:{messages:[c,...f.messages],lastMessageId:c.id}),c}catch(c){throw console.error("메시지 생성 실패:",c),c}finally{n({isCreating:!1})}},updateMessage:async(s,l)=>{try{const c=await P1(s,{newContent:l});return n(d=>({messages:d.messages.map(f=>f.id===s?{...f,content:l}:f)})),c}catch(c){throw console.error("메시지 업데이트 실패:",c),c}},deleteMessage:async s=>{try{await M1(s),n(l=>({messages:l.messages.filter(c=>c.id!==s)}))}catch(l){throw console.error("메시지 삭제 실패:",l),l}}}));function _1({channel:n}){const[i,s]=Z.useState(""),[l,c]=Z.useState([]),[d,f]=Z.useState(!1),m=Vh(k=>k.createMessage),{currentUser:x}=et(),y=async k=>{if(k.preventDefault(),!(!i.trim()&&l.length===0)&&!d){f(!0);try{await m({content:i.trim(),channelId:n.id,authorId:(x==null?void 0:x.id)??""},l),s(""),c([])}catch(A){console.error("메시지 전송 실패:",A)}finally{f(!1)}}},S=k=>{const A=Array.from(k.target.files||[]);c(_=>[..._,...A]),k.target.value=""},j=k=>{c(A=>A.filter((_,J)=>J!==k))},$=k=>{if(k.key==="Enter"&&!k.shiftKey){if(console.log("Enter key pressed"),k.preventDefault(),k.nativeEvent.isComposing)return;y(k)}},I=(k,A)=>k.type.startsWith("image/")?h.jsxs(m1,{children:[h.jsx("img",{src:URL.createObjectURL(k),alt:k.name}),h.jsx(Ap,{onClick:()=>j(A),children:"×"})]},A):h.jsxs(Yh,{children:[h.jsx(g1,{children:"📎"}),h.jsx(y1,{children:k.name}),h.jsx(Ap,{onClick:()=>j(A),children:"×"})]},A);return Z.useEffect(()=>()=>{l.forEach(k=>{k.type.startsWith("image/")&&URL.revokeObjectURL(URL.createObjectURL(k))})},[l]),n?h.jsxs(h.Fragment,{children:[l.length>0&&!d&&h.jsx(h1,{children:l.map((k,A)=>I(k,A))}),h.jsxs(c1,{onSubmit:y,children:[h.jsxs(f1,{as:"label",children:["+",h.jsx("input",{type:"file",multiple:!0,onChange:S,style:{display:"none"}})]}),h.jsx(d1,{value:i,onChange:k=>s(k.target.value),onKeyDown:$,disabled:d,placeholder:d?"메시지 전송 중...":n.type==="PUBLIC"?`#${n.name}에 메시지 보내기`:"메시지 보내기"}),d&&h.jsx(x1,{})]})]}):null}/*! *****************************************************************************
Copyright (c) Microsoft Corporation. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License"); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at http://www.apache.org/licenses/LICENSE-2.0

THIS CODE IS PROVIDED ON AN *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION ANY IMPLIED
WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A PARTICULAR PURPOSE,
MERCHANTABLITY OR NON-INFRINGEMENT.

See the Apache Version 2.0 License for specific language governing permissions
and limitations under the License.
***************************************************************************** */var hu=function(n,i){return hu=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(s,l){s.__proto__=l}||function(s,l){for(var c in l)l.hasOwnProperty(c)&&(s[c]=l[c])},hu(n,i)};function T1(n,i){hu(n,i);function s(){this.constructor=n}n.prototype=i===null?Object.create(i):(s.prototype=i.prototype,new s)}var Oo=function(){return Oo=Object.assign||function(i){for(var s,l=1,c=arguments.length;l<c;l++){s=arguments[l];for(var d in s)Object.prototype.hasOwnProperty.call(s,d)&&(i[d]=s[d])}return i},Oo.apply(this,arguments)};function N1(n,i,s,l){var c,d=!1,f=0;function m(){c&&clearTimeout(c)}function x(){m(),d=!0}typeof i!="boolean"&&(l=s,s=i,i=void 0);function y(){var S=this,j=Date.now()-f,$=arguments;if(d)return;function I(){f=Date.now(),s.apply(S,$)}function k(){c=void 0}l&&!c&&I(),m(),l===void 0&&j>n?I():i!==!0&&(c=setTimeout(l?k:I,l===void 0?n-j:n))}return y.cancel=x,y}var Rr={Pixel:"Pixel",Percent:"Percent"},_p={unit:Rr.Percent,value:.8};function Tp(n){return typeof n=="number"?{unit:Rr.Percent,value:n*100}:typeof n=="string"?n.match(/^(\d*(\.\d+)?)px$/)?{unit:Rr.Pixel,value:parseFloat(n)}:n.match(/^(\d*(\.\d+)?)%$/)?{unit:Rr.Percent,value:parseFloat(n)}:(console.warn('scrollThreshold format is invalid. Valid formats: "120px", "50%"...'),_p):(console.warn("scrollThreshold should be string or number"),_p)}var $1=function(n){T1(i,n);function i(s){var l=n.call(this,s)||this;return l.lastScrollTop=0,l.actionTriggered=!1,l.startY=0,l.currentY=0,l.dragging=!1,l.maxPullDownDistance=0,l.getScrollableTarget=function(){return l.props.scrollableTarget instanceof HTMLElement?l.props.scrollableTarget:typeof l.props.scrollableTarget=="string"?document.getElementById(l.props.scrollableTarget):(l.props.scrollableTarget===null&&console.warn(`You are trying to pass scrollableTarget but it is null. This might
        happen because the element may not have been added to DOM yet.
        See https://github.com/ankeetmaini/react-infinite-scroll-component/issues/59 for more info.
      `),null)},l.onStart=function(c){l.lastScrollTop||(l.dragging=!0,c instanceof MouseEvent?l.startY=c.pageY:c instanceof TouchEvent&&(l.startY=c.touches[0].pageY),l.currentY=l.startY,l._infScroll&&(l._infScroll.style.willChange="transform",l._infScroll.style.transition="transform 0.2s cubic-bezier(0,0,0.31,1)"))},l.onMove=function(c){l.dragging&&(c instanceof MouseEvent?l.currentY=c.pageY:c instanceof TouchEvent&&(l.currentY=c.touches[0].pageY),!(l.currentY<l.startY)&&(l.currentY-l.startY>=Number(l.props.pullDownToRefreshThreshold)&&l.setState({pullToRefreshThresholdBreached:!0}),!(l.currentY-l.startY>l.maxPullDownDistance*1.5)&&l._infScroll&&(l._infScroll.style.overflow="visible",l._infScroll.style.transform="translate3d(0px, "+(l.currentY-l.startY)+"px, 0px)")))},l.onEnd=function(){l.startY=0,l.currentY=0,l.dragging=!1,l.state.pullToRefreshThresholdBreached&&(l.props.refreshFunction&&l.props.refreshFunction(),l.setState({pullToRefreshThresholdBreached:!1})),requestAnimationFrame(function(){l._infScroll&&(l._infScroll.style.overflow="auto",l._infScroll.style.transform="none",l._infScroll.style.willChange="unset")})},l.onScrollListener=function(c){typeof l.props.onScroll=="function"&&setTimeout(function(){return l.props.onScroll&&l.props.onScroll(c)},0);var d=l.props.height||l._scrollableNode?c.target:document.documentElement.scrollTop?document.documentElement:document.body;if(!l.actionTriggered){var f=l.props.inverse?l.isElementAtTop(d,l.props.scrollThreshold):l.isElementAtBottom(d,l.props.scrollThreshold);f&&l.props.hasMore&&(l.actionTriggered=!0,l.setState({showLoader:!0}),l.props.next&&l.props.next()),l.lastScrollTop=d.scrollTop}},l.state={showLoader:!1,pullToRefreshThresholdBreached:!1,prevDataLength:s.dataLength},l.throttledOnScrollListener=N1(150,l.onScrollListener).bind(l),l.onStart=l.onStart.bind(l),l.onMove=l.onMove.bind(l),l.onEnd=l.onEnd.bind(l),l}return i.prototype.componentDidMount=function(){if(typeof this.props.dataLength>"u")throw new Error('mandatory prop "dataLength" is missing. The prop is needed when loading more content. Check README.md for usage');if(this._scrollableNode=this.getScrollableTarget(),this.el=this.props.height?this._infScroll:this._scrollableNode||window,this.el&&this.el.addEventListener("scroll",this.throttledOnScrollListener),typeof this.props.initialScrollY=="number"&&this.el&&this.el instanceof HTMLElement&&this.el.scrollHeight>this.props.initialScrollY&&this.el.scrollTo(0,this.props.initialScrollY),this.props.pullDownToRefresh&&this.el&&(this.el.addEventListener("touchstart",this.onStart),this.el.addEventListener("touchmove",this.onMove),this.el.addEventListener("touchend",this.onEnd),this.el.addEventListener("mousedown",this.onStart),this.el.addEventListener("mousemove",this.onMove),this.el.addEventListener("mouseup",this.onEnd),this.maxPullDownDistance=this._pullDown&&this._pullDown.firstChild&&this._pullDown.firstChild.getBoundingClientRect().height||0,this.forceUpdate(),typeof this.props.refreshFunction!="function"))throw new Error(`Mandatory prop "refreshFunction" missing.
          Pull Down To Refresh functionality will not work
          as expected. Check README.md for usage'`)},i.prototype.componentWillUnmount=function(){this.el&&(this.el.removeEventListener("scroll",this.throttledOnScrollListener),this.props.pullDownToRefresh&&(this.el.removeEventListener("touchstart",this.onStart),this.el.removeEventListener("touchmove",this.onMove),this.el.removeEventListener("touchend",this.onEnd),this.el.removeEventListener("mousedown",this.onStart),this.el.removeEventListener("mousemove",this.onMove),this.el.removeEventListener("mouseup",this.onEnd)))},i.prototype.componentDidUpdate=function(s){this.props.dataLength!==s.dataLength&&(this.actionTriggered=!1,this.setState({showLoader:!1}))},i.getDerivedStateFromProps=function(s,l){var c=s.dataLength!==l.prevDataLength;return c?Oo(Oo({},l),{prevDataLength:s.dataLength}):null},i.prototype.isElementAtTop=function(s,l){l===void 0&&(l=.8);var c=s===document.body||s===document.documentElement?window.screen.availHeight:s.clientHeight,d=Tp(l);return d.unit===Rr.Pixel?s.scrollTop<=d.value+c-s.scrollHeight+1:s.scrollTop<=d.value/100+c-s.scrollHeight+1},i.prototype.isElementAtBottom=function(s,l){l===void 0&&(l=.8);var c=s===document.body||s===document.documentElement?window.screen.availHeight:s.clientHeight,d=Tp(l);return d.unit===Rr.Pixel?s.scrollTop+c>=s.scrollHeight-d.value:s.scrollTop+c>=d.value/100*s.scrollHeight},i.prototype.render=function(){var s=this,l=Oo({height:this.props.height||"auto",overflow:"auto",WebkitOverflowScrolling:"touch"},this.props.style),c=this.props.hasChildren||!!(this.props.children&&this.props.children instanceof Array&&this.props.children.length),d=this.props.pullDownToRefresh&&this.props.height?{overflow:"auto"}:{};return St.createElement("div",{style:d,className:"infinite-scroll-component__outerdiv"},St.createElement("div",{className:"infinite-scroll-component "+(this.props.className||""),ref:function(f){return s._infScroll=f},style:l},this.props.pullDownToRefresh&&St.createElement("div",{style:{position:"relative"},ref:function(f){return s._pullDown=f}},St.createElement("div",{style:{position:"absolute",left:0,right:0,top:-1*this.maxPullDownDistance}},this.state.pullToRefreshThresholdBreached?this.props.releaseToRefreshContent:this.props.pullDownToRefreshContent)),this.props.children,!this.state.showLoader&&!c&&this.props.hasMore&&this.props.loader,this.state.showLoader&&this.props.hasMore&&this.props.loader,!this.props.hasMore&&this.props.endMessage))},i}(Z.Component);const O1=n=>n<1024?n+" B":n<1024*1024?(n/1024).toFixed(2)+" KB":n<1024*1024*1024?(n/(1024*1024)).toFixed(2)+" MB":(n/(1024*1024*1024)).toFixed(2)+" GB";function L1({channel:n}){const{messages:i,fetchMessages:s,loadMoreMessages:l,pagination:c,startPolling:d,stopPolling:f,updateMessage:m,deleteMessage:x}=Vh(),{binaryContents:y,fetchBinaryContent:S,clearBinaryContents:j,startPolling:$,clearAllPolling:I}=Rn(),{currentUser:k}=et(),[A,_]=Z.useState(null),[J,G]=Z.useState(null),[H,X]=Z.useState("");Z.useEffect(()=>{if(n!=null&&n.id)return s(n.id,null),d(n.id),()=>{f(n.id),I()}},[n==null?void 0:n.id,s,d,f,I]),Z.useEffect(()=>{i.forEach(V=>{var z;(z=V.attachments)==null||z.forEach(b=>{y[b.id]||S(b.id).then(W=>{W&&W.status==="PROCESSING"&&$(b.id)})})})},[i,S,$]),Z.useEffect(()=>()=>{const V=i.map(z=>{var b;return(b=z.attachments)==null?void 0:b.map(W=>W.id)}).flat();j(V),I()},[j,I]),Z.useEffect(()=>{const V=()=>{A&&_(null)};if(A)return document.addEventListener("click",V),()=>document.removeEventListener("click",V)},[A]);const D=async V=>{try{const{url:z,fileName:b}=V;if(z==null)return;const W=document.createElement("a");W.href=z,W.download=b,W.style.display="none",document.body.appendChild(W);try{const F=await(await window.showSaveFilePicker({suggestedName:V.fileName,types:[{description:"Files",accept:{"*/*":[".txt",".pdf",".doc",".docx",".xls",".xlsx",".jpg",".jpeg",".png",".gif"]}}]})).createWritable(),w=await(await fetch(z)).blob();await F.write(w),await F.close()}catch(P){P.name!=="AbortError"&&W.click()}document.body.removeChild(W),window.URL.revokeObjectURL(z)}catch(z){console.error("파일 다운로드 실패:",z)}},N=V=>V!=null&&V.length?(console.log("renderAttachments 호출됨",{attachments:V.map(z=>{var b;return{id:z.id,binaryContent:(b=y[z.id])==null?void 0:b.status}})}),V.map(z=>{const b=y[z.id];if(!b)return null;const W=b.contentType.startsWith("image/"),P=b.status;return P==="FAIL"?h.jsx(is,{children:h.jsxs(qa,{href:"#",style:{opacity:.5,backgroundColor:"#fff2f2"},onClick:F=>{F.preventDefault()},children:[h.jsx(Qa,{children:h.jsxs("svg",{width:"40",height:"40",viewBox:"0 0 40 40",fill:"none",children:[h.jsx("path",{d:"M8 3C8 1.89543 8.89543 1 10 1H22L32 11V37C32 38.1046 31.1046 39 30 39H10C8.89543 39 8 38.1046 8 37V3Z",fill:"#ef4444",fillOpacity:"0.1"}),h.jsx("path",{d:"M22 1L32 11H24C22.8954 11 22 10.1046 22 9V1Z",fill:"#ef4444",fillOpacity:"0.3"}),h.jsx("path",{d:"M13 19H27M13 25H27M13 31H27",stroke:"#ef4444",strokeWidth:"2",strokeLinecap:"round"})]})}),h.jsxs(Ga,{children:[h.jsx(Ka,{style:{color:"#ef4444"},children:z.fileName}),h.jsx(Xa,{style:{color:"#ef4444"},children:"업로드 실패"})]})]})},z.id):P==="PROCESSING"?h.jsx(is,{children:h.jsxs(qa,{href:"#",style:{opacity:.7,backgroundColor:"#fef3c7"},onClick:F=>{F.preventDefault()},children:[h.jsx(Qa,{children:h.jsxs("svg",{width:"40",height:"40",viewBox:"0 0 40 40",fill:"none",children:[h.jsx("path",{d:"M8 3C8 1.89543 8.89543 1 10 1H22L32 11V37C32 38.1046 31.1046 39 30 39H10C8.89543 39 8 38.1046 8 37V3Z",fill:"#f59e0b",fillOpacity:"0.1"}),h.jsx("path",{d:"M22 1L32 11H24C22.8954 11 22 10.1046 22 9V1Z",fill:"#f59e0b",fillOpacity:"0.3"}),h.jsx("path",{d:"M13 19H27M13 25H27M13 31H27",stroke:"#f59e0b",strokeWidth:"2",strokeLinecap:"round"})]})}),h.jsxs(Ga,{children:[h.jsx(Ka,{style:{color:"#f59e0b"},children:z.fileName}),h.jsx(Xa,{style:{color:"#f59e0b"},children:"업로드 중..."})]})]})},z.id):b.url?W?h.jsx(is,{children:h.jsx(p1,{href:"#",onClick:F=>{F.preventDefault(),D(b)},children:h.jsx("img",{src:b.url,alt:b.fileName})})},b.url):h.jsx(is,{children:h.jsxs(qa,{href:"#",onClick:F=>{F.preventDefault(),D(b)},children:[h.jsx(Qa,{children:h.jsxs("svg",{width:"40",height:"40",viewBox:"0 0 40 40",fill:"none",children:[h.jsx("path",{d:"M8 3C8 1.89543 8.89543 1 10 1H22L32 11V37C32 38.1046 31.1046 39 30 39H10C8.89543 39 8 38.1046 8 37V3Z",fill:"#0B93F6",fillOpacity:"0.1"}),h.jsx("path",{d:"M22 1L32 11H24C22.8954 11 22 10.1046 22 9V1Z",fill:"#0B93F6",fillOpacity:"0.3"}),h.jsx("path",{d:"M13 19H27M13 25H27M13 31H27",stroke:"#0B93F6",strokeWidth:"2",strokeLinecap:"round"})]})}),h.jsxs(Ga,{children:[h.jsx(Ka,{children:b.fileName}),h.jsx(Xa,{children:O1(b.size)})]})]})},b.url):null})):null,Q=V=>new Date(V).toLocaleTimeString(),le=()=>{n!=null&&n.id&&l(n.id)},Se=V=>{_(A===V?null:V)},ge=V=>{_(null);const z=i.find(b=>b.id===V);z&&(G(V),X(z.content))},pe=V=>{m(V,H).catch(z=>{console.error("메시지 수정 실패:",z),jr.emit("api-error",{error:z,alert:!0})}),G(null),X("")},Be=()=>{G(null),X("")},Fe=V=>{_(null),x(V)};return h.jsx(r1,{children:h.jsx("div",{id:"scrollableDiv",style:{height:"100%",overflow:"auto",display:"flex",flexDirection:"column-reverse"},children:h.jsx($1,{dataLength:i.length,next:le,hasMore:c.hasNext,loader:h.jsx("h4",{style:{textAlign:"center"},children:"메시지를 불러오는 중..."}),scrollableTarget:"scrollableDiv",style:{display:"flex",flexDirection:"column-reverse"},inverse:!0,endMessage:h.jsx("p",{style:{textAlign:"center"},children:h.jsx("b",{children:c.nextCursor!==null?"모든 메시지를 불러왔습니다":""})}),children:h.jsx(o1,{children:[...i].reverse().map(V=>{var W;const z=V.author,b=k&&z&&z.id===k.id;return h.jsxs(Hh,{children:[h.jsx(i1,{children:h.jsx(rn,{src:z&&z.profile?(W=y[z.profile.id])==null?void 0:W.url:Ct,alt:z&&z.username||"알 수 없음"})}),h.jsxs("div",{children:[h.jsxs(s1,{children:[h.jsx(l1,{children:z&&z.username||"알 수 없음"}),h.jsx(a1,{children:Q(V.createdAt)}),b&&h.jsxs(v1,{children:[h.jsx(w1,{onClick:P=>{P.stopPropagation(),Se(V.id)},children:"⋯"}),A===V.id&&h.jsxs(S1,{onClick:P=>P.stopPropagation(),children:[h.jsx(Rp,{onClick:()=>ge(V.id),children:"✏️ 수정"}),h.jsx(Rp,{onClick:()=>Fe(V.id),children:"🗑️ 삭제"})]})]})]}),J===V.id?h.jsxs(k1,{children:[h.jsx(C1,{value:H,onChange:P=>X(P.target.value),onKeyDown:P=>{P.key==="Escape"?Be():P.key==="Enter"&&(P.ctrlKey||P.metaKey)&&(P.preventDefault(),pe(V.id))},placeholder:"메시지를 입력하세요..."}),h.jsxs(E1,{children:[h.jsx(Pp,{variant:"secondary",onClick:Be,children:"취소"}),h.jsx(Pp,{variant:"primary",onClick:()=>pe(V.id),children:"저장"})]})]}):h.jsx(u1,{children:V.content}),N(V.attachments)]})]},V.id)})})})})})}function D1({channel:n}){return n?h.jsxs(Vv,{children:[h.jsx(j1,{channel:n}),h.jsx(L1,{channel:n}),h.jsx(_1,{channel:n})]}):h.jsx(qv,{children:h.jsxs(Qv,{children:[h.jsx(Gv,{children:"👋"}),h.jsx(Kv,{children:"채널을 선택해주세요"}),h.jsxs(Xv,{children:["왼쪽의 채널 목록에서 채널을 선택하여",h.jsx("br",{}),"대화를 시작하세요."]})]})})}function I1(n,i="yyyy-MM-dd HH:mm:ss"){if(!n||!(n instanceof Date)||isNaN(n.getTime()))return"";const s=n.getFullYear(),l=String(n.getMonth()+1).padStart(2,"0"),c=String(n.getDate()).padStart(2,"0"),d=String(n.getHours()).padStart(2,"0"),f=String(n.getMinutes()).padStart(2,"0"),m=String(n.getSeconds()).padStart(2,"0");return i.replace("yyyy",s.toString()).replace("MM",l).replace("dd",c).replace("HH",d).replace("mm",f).replace("ss",m)}const b1=E.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
`,z1=E.div`
  background: ${({theme:n})=>n.colors.background.primary};
  border-radius: 8px;
  width: 500px;
  max-width: 90%;
  padding: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
`,B1=E.div`
  display: flex;
  align-items: center;
  margin-bottom: 16px;
`,F1=E.div`
  color: ${({theme:n})=>n.colors.status.error};
  font-size: 24px;
  margin-right: 12px;
`,U1=E.h3`
  color: ${({theme:n})=>n.colors.text.primary};
  margin: 0;
  font-size: 18px;
`,H1=E.div`
  background: ${({theme:n})=>n.colors.background.tertiary};
  color: ${({theme:n})=>n.colors.text.muted};
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 14px;
  margin-left: auto;
`,Y1=E.p`
  color: ${({theme:n})=>n.colors.text.secondary};
  margin-bottom: 20px;
  line-height: 1.5;
  font-weight: 500;
`,V1=E.div`
  margin-bottom: 20px;
  background: ${({theme:n})=>n.colors.background.secondary};
  border-radius: 6px;
  padding: 12px;
`,Ao=E.div`
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
`,Ro=E.span`
  color: ${({theme:n})=>n.colors.text.muted};
  min-width: 100px;
`,Po=E.span`
  color: ${({theme:n})=>n.colors.text.secondary};
  word-break: break-word;
`,W1=E.button`
  background: ${({theme:n})=>n.colors.brand.primary};
  color: white;
  border: none;
  border-radius: 4px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  width: 100%;
  
  &:hover {
    background: ${({theme:n})=>n.colors.brand.hover};
  }
`;function q1({isOpen:n,onClose:i,error:s}){var $,I;if(!n)return null;console.log({error:s});const l=($=s==null?void 0:s.response)==null?void 0:$.data,c=(l==null?void 0:l.status)||((I=s==null?void 0:s.response)==null?void 0:I.status)||"오류",d=(l==null?void 0:l.code)||"",f=(l==null?void 0:l.message)||(s==null?void 0:s.message)||"알 수 없는 오류가 발생했습니다.",m=l!=null&&l.timestamp?new Date(l.timestamp):new Date,x=I1(m),y=(l==null?void 0:l.exceptionType)||"",S=(l==null?void 0:l.details)||{},j=(l==null?void 0:l.requestId)||"";return h.jsx(b1,{onClick:i,children:h.jsxs(z1,{onClick:k=>k.stopPropagation(),children:[h.jsxs(B1,{children:[h.jsx(F1,{children:"⚠️"}),h.jsx(U1,{children:"오류가 발생했습니다"}),h.jsxs(H1,{children:[c,d?` (${d})`:""]})]}),h.jsx(Y1,{children:f}),h.jsxs(V1,{children:[h.jsxs(Ao,{children:[h.jsx(Ro,{children:"시간:"}),h.jsx(Po,{children:x})]}),j&&h.jsxs(Ao,{children:[h.jsx(Ro,{children:"요청 ID:"}),h.jsx(Po,{children:j})]}),d&&h.jsxs(Ao,{children:[h.jsx(Ro,{children:"에러 코드:"}),h.jsx(Po,{children:d})]}),y&&h.jsxs(Ao,{children:[h.jsx(Ro,{children:"예외 유형:"}),h.jsx(Po,{children:y})]}),Object.keys(S).length>0&&h.jsxs(Ao,{children:[h.jsx(Ro,{children:"상세 정보:"}),h.jsx(Po,{children:Object.entries(S).map(([k,A])=>h.jsxs("div",{children:[k,": ",String(A)]},k))})]})]}),h.jsx(W1,{onClick:i,children:"확인"})]})})}const Q1=E.div`
  width: 240px;
  background: ${ee.colors.background.secondary};
  border-left: 1px solid ${ee.colors.border.primary};
  display: flex;
  flex-direction: column;
  height: 100%;
`,G1=E.div`
  padding: 0px 16px;
  height: 48px;
  font-size: 14px;
  font-weight: bold;
  color: ${ee.colors.text.muted};
  text-transform: uppercase;
  border-bottom: 1px solid ${ee.colors.border.primary};
`,K1=E.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`,X1=E.div`
  padding: 8px 16px;
  display: flex;
  align-items: center;
  color: ${ee.colors.text.muted};
  &:hover {
    background: ${ee.colors.background.primary};
    cursor: pointer;
  }
`,J1=E(Dr)`
  margin-right: 12px;
`;E(rn)``;const Z1=E.div`
  display: flex;
  align-items: center;
`;function ew({member:n}){var l,c,d;const{binaryContents:i,fetchBinaryContent:s}=Rn();return Z.useEffect(()=>{var f;(f=n.profile)!=null&&f.id&&!i[n.profile.id]&&s(n.profile.id)},[(l=n.profile)==null?void 0:l.id,i,s]),h.jsxs(X1,{children:[h.jsxs(J1,{children:[h.jsx(rn,{src:(c=n.profile)!=null&&c.id&&((d=i[n.profile.id])==null?void 0:d.url)||Ct,alt:n.username}),h.jsx(Fo,{$online:n.online})]}),h.jsx(Z1,{children:n.username})]})}function tw({member:n,onClose:i}){var I,k,A;const{binaryContents:s,fetchBinaryContent:l}=Rn(),{currentUser:c,updateUserRole:d}=et(),[f,m]=Z.useState(n.role),[x,y]=Z.useState(!1);Z.useEffect(()=>{var _;(_=n.profile)!=null&&_.id&&!s[n.profile.id]&&l(n.profile.id)},[(I=n.profile)==null?void 0:I.id,s,l]);const S={[jn.USER]:{name:"사용자",color:"#2ed573"},[jn.CHANNEL_MANAGER]:{name:"채널 관리자",color:"#ff4757"},[jn.ADMIN]:{name:"어드민",color:"#0097e6"}},j=_=>{m(_),y(!0)},$=()=>{d(n.id,f),y(!1)};return h.jsx(ow,{onClick:i,children:h.jsxs(iw,{onClick:_=>_.stopPropagation(),children:[h.jsx("h2",{children:"사용자 정보"}),h.jsxs(sw,{children:[h.jsx(lw,{src:(k=n.profile)!=null&&k.id&&((A=s[n.profile.id])==null?void 0:A.url)||Ct,alt:n.username}),h.jsx(aw,{children:n.username}),h.jsx(uw,{children:n.email}),h.jsx(cw,{$online:n.online,children:n.online?"온라인":"오프라인"}),(c==null?void 0:c.role)===jn.ADMIN?h.jsx(rw,{value:f,onChange:_=>j(_.target.value),children:Object.entries(S).map(([_,J])=>h.jsx("option",{value:_,style:{marginTop:"8px",textAlign:"center"},children:J.name},_))}):h.jsx(nw,{style:{backgroundColor:S[n.role].color},children:S[n.role].name})]}),h.jsx(dw,{children:(c==null?void 0:c.role)===jn.ADMIN&&x&&h.jsx(fw,{onClick:$,disabled:!x,$secondary:!x,children:"저장"})})]})})}const nw=E.div`
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: white;
  margin-top: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.3px;
`,rw=E.select`
  padding: 10px 16px;
  border-radius: 8px;
  border: 1.5px solid ${ee.colors.border.primary};
  background: ${ee.colors.background.primary};
  color: ${ee.colors.text.primary};
  font-size: 14px;
  width: 140px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-top: 12px;
  font-weight: 500;

  &:hover {
    border-color: ${ee.colors.brand.primary};
  }

  &:focus {
    outline: none;
    border-color: ${ee.colors.brand.primary};
    box-shadow: 0 0 0 2px ${ee.colors.brand.primary}20;
  }

  option {
    background: ${ee.colors.background.primary};
    color: ${ee.colors.text.primary};
    padding: 12px;
  }
`,ow=E.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
`,iw=E.div`
  background: ${ee.colors.background.secondary};
  padding: 40px;
  border-radius: 16px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);

  h2 {
    color: ${ee.colors.text.primary};
    margin-bottom: 32px;
    text-align: center;
    font-size: 26px;
    font-weight: 600;
    letter-spacing: -0.5px;
  }
`,sw=E.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
  padding: 24px;
  background: ${ee.colors.background.primary};
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
`,lw=E.img`
  width: 140px;
  height: 140px;
  border-radius: 50%;
  margin-bottom: 20px;
  object-fit: cover;
  border: 4px solid ${ee.colors.background.secondary};
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
`,aw=E.div`
  font-size: 22px;
  font-weight: 600;
  color: ${ee.colors.text.primary};
  margin-bottom: 8px;
  letter-spacing: -0.3px;
`,uw=E.div`
  font-size: 14px;
  color: ${ee.colors.text.muted};
  margin-bottom: 16px;
  font-weight: 500;
`,cw=E.div`
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  background-color: ${({$online:n,theme:i})=>n?i.colors.status.online:i.colors.status.offline};
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.3px;
`,dw=E.div`
  display: flex;
  gap: 12px;
  margin-top: 24px;
`,fw=E.button`
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 8px;
  background: ${({$secondary:n,theme:i})=>n?"transparent":i.colors.brand.primary};
  color: ${({$secondary:n,theme:i})=>n?i.colors.text.primary:"white"};
  cursor: pointer;
  font-weight: 600;
  font-size: 15px;
  transition: all 0.2s ease;
  border: ${({$secondary:n,theme:i})=>n?`1.5px solid ${i.colors.border.primary}`:"none"};
  
  &:hover {
    background: ${({$secondary:n,theme:i})=>n?i.colors.background.hover:i.colors.brand.hover};
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }
`,pw=async()=>(await Le.get("/notifications")).data,hw=async n=>{await Le.delete(`/notifications/${n}`)},Wh=Kn(n=>({notifications:[],fetchNotifications:async()=>{const i=await pw();n({notifications:i})},readNotification:async i=>{await hw(i),n(s=>({notifications:s.notifications.filter(l=>l.id!==i)}))}}));var hs={exports:{}},mw=hs.exports,Np;function qh(){return Np||(Np=1,function(n,i){(function(s,l){n.exports=l()})(mw,function(){var s=1e3,l=6e4,c=36e5,d="millisecond",f="second",m="minute",x="hour",y="day",S="week",j="month",$="quarter",I="year",k="date",A="Invalid Date",_=/^(\d{4})[-/]?(\d{1,2})?[-/]?(\d{0,2})[Tt\s]*(\d{1,2})?:?(\d{1,2})?:?(\d{1,2})?[.:]?(\d+)?$/,J=/\[([^\]]+)]|Y{1,4}|M{1,4}|D{1,2}|d{1,4}|H{1,2}|h{1,2}|a|A|m{1,2}|s{1,2}|Z{1,2}|SSS/g,G={name:"en",weekdays:"Sunday_Monday_Tuesday_Wednesday_Thursday_Friday_Saturday".split("_"),months:"January_February_March_April_May_June_July_August_September_October_November_December".split("_"),ordinal:function(V){var z=["th","st","nd","rd"],b=V%100;return"["+V+(z[(b-20)%10]||z[b]||z[0])+"]"}},H=function(V,z,b){var W=String(V);return!W||W.length>=z?V:""+Array(z+1-W.length).join(b)+V},X={s:H,z:function(V){var z=-V.utcOffset(),b=Math.abs(z),W=Math.floor(b/60),P=b%60;return(z<=0?"+":"-")+H(W,2,"0")+":"+H(P,2,"0")},m:function V(z,b){if(z.date()<b.date())return-V(b,z);var W=12*(b.year()-z.year())+(b.month()-z.month()),P=z.clone().add(W,j),F=b-P<0,B=z.clone().add(W+(F?-1:1),j);return+(-(W+(b-P)/(F?P-B:B-P))||0)},a:function(V){return V<0?Math.ceil(V)||0:Math.floor(V)},p:function(V){return{M:j,y:I,w:S,d:y,D:k,h:x,m,s:f,ms:d,Q:$}[V]||String(V||"").toLowerCase().replace(/s$/,"")},u:function(V){return V===void 0}},D="en",N={};N[D]=G;var Q="$isDayjsObject",le=function(V){return V instanceof Be||!(!V||!V[Q])},Se=function V(z,b,W){var P;if(!z)return D;if(typeof z=="string"){var F=z.toLowerCase();N[F]&&(P=F),b&&(N[F]=b,P=F);var B=z.split("-");if(!P&&B.length>1)return V(B[0])}else{var w=z.name;N[w]=z,P=w}return!W&&P&&(D=P),P||!W&&D},ge=function(V,z){if(le(V))return V.clone();var b=typeof z=="object"?z:{};return b.date=V,b.args=arguments,new Be(b)},pe=X;pe.l=Se,pe.i=le,pe.w=function(V,z){return ge(V,{locale:z.$L,utc:z.$u,x:z.$x,$offset:z.$offset})};var Be=function(){function V(b){this.$L=Se(b.locale,null,!0),this.parse(b),this.$x=this.$x||b.x||{},this[Q]=!0}var z=V.prototype;return z.parse=function(b){this.$d=function(W){var P=W.date,F=W.utc;if(P===null)return new Date(NaN);if(pe.u(P))return new Date;if(P instanceof Date)return new Date(P);if(typeof P=="string"&&!/Z$/i.test(P)){var B=P.match(_);if(B){var w=B[2]-1||0,L=(B[7]||"0").substring(0,3);return F?new Date(Date.UTC(B[1],w,B[3]||1,B[4]||0,B[5]||0,B[6]||0,L)):new Date(B[1],w,B[3]||1,B[4]||0,B[5]||0,B[6]||0,L)}}return new Date(P)}(b),this.init()},z.init=function(){var b=this.$d;this.$y=b.getFullYear(),this.$M=b.getMonth(),this.$D=b.getDate(),this.$W=b.getDay(),this.$H=b.getHours(),this.$m=b.getMinutes(),this.$s=b.getSeconds(),this.$ms=b.getMilliseconds()},z.$utils=function(){return pe},z.isValid=function(){return this.$d.toString()!==A},z.isSame=function(b,W){var P=ge(b);return this.startOf(W)<=P&&P<=this.endOf(W)},z.isAfter=function(b,W){return ge(b)<this.startOf(W)},z.isBefore=function(b,W){return this.endOf(W)<ge(b)},z.$g=function(b,W,P){return pe.u(b)?this[W]:this.set(P,b)},z.unix=function(){return Math.floor(this.valueOf()/1e3)},z.valueOf=function(){return this.$d.getTime()},z.startOf=function(b,W){var P=this,F=!!pe.u(W)||W,B=pe.p(b),w=function(xe,Ee){var We=pe.w(P.$u?Date.UTC(P.$y,Ee,xe):new Date(P.$y,Ee,xe),P);return F?We:We.endOf(y)},L=function(xe,Ee){return pe.w(P.toDate()[xe].apply(P.toDate("s"),(F?[0,0,0,0]:[23,59,59,999]).slice(Ee)),P)},ie=this.$W,ae=this.$M,de=this.$D,he="set"+(this.$u?"UTC":"");switch(B){case I:return F?w(1,0):w(31,11);case j:return F?w(1,ae):w(0,ae+1);case S:var we=this.$locale().weekStart||0,ye=(ie<we?ie+7:ie)-we;return w(F?de-ye:de+(6-ye),ae);case y:case k:return L(he+"Hours",0);case x:return L(he+"Minutes",1);case m:return L(he+"Seconds",2);case f:return L(he+"Milliseconds",3);default:return this.clone()}},z.endOf=function(b){return this.startOf(b,!1)},z.$set=function(b,W){var P,F=pe.p(b),B="set"+(this.$u?"UTC":""),w=(P={},P[y]=B+"Date",P[k]=B+"Date",P[j]=B+"Month",P[I]=B+"FullYear",P[x]=B+"Hours",P[m]=B+"Minutes",P[f]=B+"Seconds",P[d]=B+"Milliseconds",P)[F],L=F===y?this.$D+(W-this.$W):W;if(F===j||F===I){var ie=this.clone().set(k,1);ie.$d[w](L),ie.init(),this.$d=ie.set(k,Math.min(this.$D,ie.daysInMonth())).$d}else w&&this.$d[w](L);return this.init(),this},z.set=function(b,W){return this.clone().$set(b,W)},z.get=function(b){return this[pe.p(b)]()},z.add=function(b,W){var P,F=this;b=Number(b);var B=pe.p(W),w=function(ae){var de=ge(F);return pe.w(de.date(de.date()+Math.round(ae*b)),F)};if(B===j)return this.set(j,this.$M+b);if(B===I)return this.set(I,this.$y+b);if(B===y)return w(1);if(B===S)return w(7);var L=(P={},P[m]=l,P[x]=c,P[f]=s,P)[B]||1,ie=this.$d.getTime()+b*L;return pe.w(ie,this)},z.subtract=function(b,W){return this.add(-1*b,W)},z.format=function(b){var W=this,P=this.$locale();if(!this.isValid())return P.invalidDate||A;var F=b||"YYYY-MM-DDTHH:mm:ssZ",B=pe.z(this),w=this.$H,L=this.$m,ie=this.$M,ae=P.weekdays,de=P.months,he=P.meridiem,we=function(Ee,We,Xe,qt){return Ee&&(Ee[We]||Ee(W,F))||Xe[We].slice(0,qt)},ye=function(Ee){return pe.s(w%12||12,Ee,"0")},xe=he||function(Ee,We,Xe){var qt=Ee<12?"AM":"PM";return Xe?qt.toLowerCase():qt};return F.replace(J,function(Ee,We){return We||function(Xe){switch(Xe){case"YY":return String(W.$y).slice(-2);case"YYYY":return pe.s(W.$y,4,"0");case"M":return ie+1;case"MM":return pe.s(ie+1,2,"0");case"MMM":return we(P.monthsShort,ie,de,3);case"MMMM":return we(de,ie);case"D":return W.$D;case"DD":return pe.s(W.$D,2,"0");case"d":return String(W.$W);case"dd":return we(P.weekdaysMin,W.$W,ae,2);case"ddd":return we(P.weekdaysShort,W.$W,ae,3);case"dddd":return ae[W.$W];case"H":return String(w);case"HH":return pe.s(w,2,"0");case"h":return ye(1);case"hh":return ye(2);case"a":return xe(w,L,!0);case"A":return xe(w,L,!1);case"m":return String(L);case"mm":return pe.s(L,2,"0");case"s":return String(W.$s);case"ss":return pe.s(W.$s,2,"0");case"SSS":return pe.s(W.$ms,3,"0");case"Z":return B}return null}(Ee)||B.replace(":","")})},z.utcOffset=function(){return 15*-Math.round(this.$d.getTimezoneOffset()/15)},z.diff=function(b,W,P){var F,B=this,w=pe.p(W),L=ge(b),ie=(L.utcOffset()-this.utcOffset())*l,ae=this-L,de=function(){return pe.m(B,L)};switch(w){case I:F=de()/12;break;case j:F=de();break;case $:F=de()/3;break;case S:F=(ae-ie)/6048e5;break;case y:F=(ae-ie)/864e5;break;case x:F=ae/c;break;case m:F=ae/l;break;case f:F=ae/s;break;default:F=ae}return P?F:pe.a(F)},z.daysInMonth=function(){return this.endOf(j).$D},z.$locale=function(){return N[this.$L]},z.locale=function(b,W){if(!b)return this.$L;var P=this.clone(),F=Se(b,W,!0);return F&&(P.$L=F),P},z.clone=function(){return pe.w(this.$d,this)},z.toDate=function(){return new Date(this.valueOf())},z.toJSON=function(){return this.isValid()?this.toISOString():null},z.toISOString=function(){return this.$d.toISOString()},z.toString=function(){return this.$d.toUTCString()},V}(),Fe=Be.prototype;return ge.prototype=Fe,[["$ms",d],["$s",f],["$m",m],["$H",x],["$W",y],["$M",j],["$y",I],["$D",k]].forEach(function(V){Fe[V[1]]=function(z){return this.$g(z,V[0],V[1])}}),ge.extend=function(V,z){return V.$i||(V(z,Be,ge),V.$i=!0),ge},ge.locale=Se,ge.isDayjs=le,ge.unix=function(V){return ge(1e3*V)},ge.en=N[D],ge.Ls=N,ge.p={},ge})}(hs)),hs.exports}var gw=qh();const Ru=mu(gw);var ms={exports:{}},yw=ms.exports,$p;function xw(){return $p||($p=1,function(n,i){(function(s,l){n.exports=l()})(yw,function(){return function(s,l,c){s=s||{};var d=l.prototype,f={future:"in %s",past:"%s ago",s:"a few seconds",m:"a minute",mm:"%d minutes",h:"an hour",hh:"%d hours",d:"a day",dd:"%d days",M:"a month",MM:"%d months",y:"a year",yy:"%d years"};function m(y,S,j,$){return d.fromToBase(y,S,j,$)}c.en.relativeTime=f,d.fromToBase=function(y,S,j,$,I){for(var k,A,_,J=j.$locale().relativeTime||f,G=s.thresholds||[{l:"s",r:44,d:"second"},{l:"m",r:89},{l:"mm",r:44,d:"minute"},{l:"h",r:89},{l:"hh",r:21,d:"hour"},{l:"d",r:35},{l:"dd",r:25,d:"day"},{l:"M",r:45},{l:"MM",r:10,d:"month"},{l:"y",r:17},{l:"yy",d:"year"}],H=G.length,X=0;X<H;X+=1){var D=G[X];D.d&&(k=$?c(y).diff(j,D.d,!0):j.diff(y,D.d,!0));var N=(s.rounding||Math.round)(Math.abs(k));if(_=k>0,N<=D.r||!D.r){N<=1&&X>0&&(D=G[X-1]);var Q=J[D.l];I&&(N=I(""+N)),A=typeof Q=="string"?Q.replace("%d",N):Q(N,S,D.l,_);break}}if(S)return A;var le=_?J.future:J.past;return typeof le=="function"?le(A):le.replace("%s",A)},d.to=function(y,S){return m(y,S,this,!0)},d.from=function(y,S){return m(y,S,this)};var x=function(y){return y.$u?c.utc():c()};d.toNow=function(y){return this.to(x(this),y)},d.fromNow=function(y){return this.from(x(this),y)}}})}(ms)),ms.exports}var vw=xw();const ww=mu(vw);var gs={exports:{}},Sw=gs.exports,Op;function kw(){return Op||(Op=1,function(n,i){(function(s,l){n.exports=l(qh())})(Sw,function(s){function l(f){return f&&typeof f=="object"&&"default"in f?f:{default:f}}var c=l(s),d={name:"ko",weekdays:"일요일_월요일_화요일_수요일_목요일_금요일_토요일".split("_"),weekdaysShort:"일_월_화_수_목_금_토".split("_"),weekdaysMin:"일_월_화_수_목_금_토".split("_"),months:"1월_2월_3월_4월_5월_6월_7월_8월_9월_10월_11월_12월".split("_"),monthsShort:"1월_2월_3월_4월_5월_6월_7월_8월_9월_10월_11월_12월".split("_"),ordinal:function(f){return f+"일"},formats:{LT:"A h:mm",LTS:"A h:mm:ss",L:"YYYY.MM.DD.",LL:"YYYY년 MMMM D일",LLL:"YYYY년 MMMM D일 A h:mm",LLLL:"YYYY년 MMMM D일 dddd A h:mm",l:"YYYY.MM.DD.",ll:"YYYY년 MMMM D일",lll:"YYYY년 MMMM D일 A h:mm",llll:"YYYY년 MMMM D일 dddd A h:mm"},meridiem:function(f){return f<12?"오전":"오후"},relativeTime:{future:"%s 후",past:"%s 전",s:"몇 초",m:"1분",mm:"%d분",h:"한 시간",hh:"%d시간",d:"하루",dd:"%d일",M:"한 달",MM:"%d달",y:"일 년",yy:"%d년"}};return c.default.locale(d,null,!0),d})}(gs)),gs.exports}kw();Ru.extend(ww);Ru.locale("ko");const Cw=E.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  opacity: ${({$isOpen:n})=>n?1:0};
  visibility: ${({$isOpen:n})=>n?"visible":"hidden"};
  transition: all 0.3s ease;
  z-index: 1000;
`,Ew=E.div`
  position: fixed;
  top: 0;
  right: 0;
  width: 360px;
  height: 100vh;
  background: ${({theme:n})=>n.colors.background.primary};
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
  transform: translateX(${({$isOpen:n})=>n?"0":"100%"});
  transition: transform 0.3s ease;
  z-index: 1001;
  display: flex;
  flex-direction: column;
`,jw=E.div`
  padding: 0px 16px;
  height: 48px;
  font-size: 14px;
  font-weight: bold;
  color: ${ee.colors.text.muted};
  text-transform: uppercase;
  border-bottom: 1px solid ${({theme:n})=>n.colors.border.primary};
  display: flex;
  justify-content: space-between;
  align-items: center;
`,Aw=E.h2`
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: ${({theme:n})=>n.colors.text.primary};
  text-transform: none;
`,Rw=E.button`
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: ${({theme:n})=>n.colors.text.muted};
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &:hover {
    background: ${({theme:n})=>n.colors.background.hover};
    color: ${({theme:n})=>n.colors.text.primary};
  }
`,Pw=E.div`
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  box-sizing: border-box;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: ${({theme:n})=>n.colors.background.primary};
  }

  &::-webkit-scrollbar-thumb {
    background: ${({theme:n})=>n.colors.border.primary};
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb:hover {
    background: ${({theme:n})=>n.colors.text.muted};
  }
`,Mw=E.div`
  position: relative;
  flex: 1;
`,Qh=E.button`
  position: absolute;
  top: 0;
  right: 0;
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: ${({theme:n})=>n.colors.text.muted};
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  opacity: 0;

  &:hover {
    background: ${({theme:n})=>n.colors.background.hover};
    color: ${({theme:n})=>n.colors.text.primary};
  }
`,_w=E.div`
  background: ${({theme:n})=>n.colors.background.primary};
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-left: 4px solid ${({theme:n})=>n.colors.brand.primary};
  width: 100%;
  box-sizing: border-box;
  position: relative;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

    ${Qh} {
      opacity: 1;
    }
  }
`,Tw=E.h4`
  color: ${({theme:n})=>n.colors.text.primary};
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 300px;
  text-transform: none;
`,Nw=E.p`
  color: ${({theme:n})=>n.colors.text.secondary};
  margin: 0 0 8px 0;
  font-size: 14px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 300px;
  word-break: break-word;
  text-transform: none;
`,$w=E.span`
  color: ${({theme:n})=>n.colors.text.muted};
  font-size: 12px;
`,Ow=E.div`
  text-align: center;
  padding: 32px 16px;
  color: ${({theme:n})=>n.colors.text.muted};
  font-size: 14px;
`,Lw=E.div`
  position: absolute;
  top: -30px;
  right: 0;
  background: ${({theme:n})=>n.colors.background.secondary};
  color: ${({theme:n})=>n.colors.text.primary};
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  opacity: ${({$show:n})=>n?1:0};
  transition: opacity 0.2s ease;
  pointer-events: none;
  white-space: nowrap;
`,Dw=({isOpen:n,onClose:i})=>{const{notifications:s,readNotification:l}=Wh(),[c,d]=Z.useState(null),f=async x=>{await l(x.id)},m=async(x,y,S)=>{x.stopPropagation();try{await navigator.clipboard.writeText(y),d(S),setTimeout(()=>d(null),2e3)}catch(j){console.error("클립보드 복사 실패:",j)}};return h.jsxs(h.Fragment,{children:[h.jsx(Cw,{$isOpen:n,onClick:i}),h.jsxs(Ew,{$isOpen:n,children:[h.jsxs(jw,{children:[h.jsxs(Aw,{children:["알림 ",s.length>0&&`(${s.length})`]}),h.jsx(Rw,{onClick:i,children:h.jsxs("svg",{width:"20",height:"20",viewBox:"0 0 24 24",fill:"none",stroke:"currentColor",strokeWidth:"2",strokeLinecap:"round",strokeLinejoin:"round",children:[h.jsx("line",{x1:"18",y1:"6",x2:"6",y2:"18"}),h.jsx("line",{x1:"6",y1:"6",x2:"18",y2:"18"})]})})]}),h.jsx(Pw,{children:s.length===0?h.jsx(Ow,{children:"새로운 알림이 없습니다"}):s.map(x=>h.jsx(_w,{onClick:()=>f(x),children:h.jsxs(Mw,{children:[h.jsx(Tw,{children:x.title}),h.jsx(Nw,{children:x.content}),h.jsx($w,{children:Ru(new Date(x.createdAt)).fromNow()}),h.jsx(Qh,{onClick:y=>m(y,x.content,x.id),title:"내용 복사",children:h.jsxs("svg",{width:"16",height:"16",viewBox:"0 0 24 24",fill:"none",stroke:"currentColor",strokeWidth:"2",strokeLinecap:"round",strokeLinejoin:"round",children:[h.jsx("rect",{x:"9",y:"9",width:"13",height:"13",rx:"2",ry:"2"}),h.jsx("path",{d:"M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"})]})}),h.jsx(Lw,{$show:c===x.id,children:"복사되었습니다"})]})},x.id))})]})]})},Iw=E.div`
  position: relative;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: background-color 0.2s ease;

  &:hover {
    background-color: ${({theme:n})=>n.colors.background.hover};
  }
`,bw=E.div`
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: ${({theme:n})=>n.colors.status.error};
  color: white;
  font-size: 12px;
  font-weight: 600;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  transform: translate(25%, -25%);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`,zw=()=>{const{notifications:n,fetchNotifications:i}=Wh(),[s,l]=Z.useState(!1);Z.useEffect(()=>{i();const d=setInterval(i,1e4);return()=>clearInterval(d)},[i]);const c=n.length;return h.jsxs(h.Fragment,{children:[h.jsxs(Iw,{onClick:()=>l(!0),children:[h.jsxs("svg",{width:"24",height:"24",viewBox:"0 0 24 24",fill:"none",stroke:"currentColor",strokeWidth:"2",strokeLinecap:"round",strokeLinejoin:"round",children:[h.jsx("path",{d:"M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"}),h.jsx("path",{d:"M13.73 21a2 2 0 0 1-3.46 0"})]}),c>0&&h.jsx(bw,{children:c>99?"99+":c})]}),h.jsx(Dw,{isOpen:s,onClose:()=>l(!1)})]})};function Bw(){const n=Nr(f=>f.users),i=Nr(f=>f.fetchUsers),{currentUser:s}=et(),[l,c]=Z.useState(null);Z.useEffect(()=>{i()},[i]);const d=[...n].sort((f,m)=>f.id===(s==null?void 0:s.id)?-1:m.id===(s==null?void 0:s.id)?1:f.online&&!m.online?-1:!f.online&&m.online?1:f.username.localeCompare(m.username));return h.jsxs(Q1,{children:[h.jsx(G1,{children:h.jsxs(K1,{children:["멤버 목록 - ",n.length,h.jsx(zw,{})]})}),d.map(f=>h.jsx("div",{onClick:()=>c(f),children:h.jsx(ew,{member:f},f.id)},f.id)),l&&h.jsx(tw,{member:l,onClose:()=>c(null)})]})}function Fw(){const{logout:n,fetchCsrfToken:i,refreshToken:s}=et(),{fetchUsers:l}=Nr(),[c,d]=Z.useState(null),[f,m]=Z.useState(null),[x,y]=Z.useState(!1),[S,j]=Z.useState(!0),{currentUser:$}=et();Z.useEffect(()=>{i(),s()},[]),Z.useEffect(()=>{(async()=>{try{if($)try{await l()}catch(A){console.warn("사용자 상태 업데이트 실패. 로그아웃합니다.",A),n()}}catch(A){console.error("초기화 오류:",A)}finally{j(!1)}})()},[$,l,n]),Z.useEffect(()=>{const k=G=>{G!=null&&G.error&&m(G.error),G!=null&&G.alert&&y(!0)},A=()=>{n()},_=jr.on("api-error",k),J=jr.on("auth-error",A);return()=>{_("api-error",k),J("auth-error",A)}},[n]),Z.useEffect(()=>{if($){const k=setInterval(()=>{l()},6e4);return()=>{clearInterval(k)}}},[$,l]);const I=()=>{y(!1),m(null)};return S?h.jsx(Vf,{theme:ee,children:h.jsx(Hw,{children:h.jsx(Yw,{})})}):h.jsxs(Vf,{theme:ee,children:[$?h.jsxs(Uw,{children:[h.jsx(Hv,{currentUser:$,activeChannel:c,onChannelSelect:d}),h.jsx(D1,{channel:c}),h.jsx(Bw,{})]}):h.jsx(ev,{isOpen:!0,onClose:()=>{}}),h.jsx(q1,{isOpen:x,onClose:I,error:f})]})}const Uw=E.div`
  display: flex;
  height: 100vh;
  width: 100vw;
  position: relative;
`,Hw=E.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100vw;
  background-color: ${({theme:n})=>n.colors.background.primary};
`,Yw=E.div`
  width: 40px;
  height: 40px;
  border: 4px solid ${({theme:n})=>n.colors.background.tertiary};
  border-top: 4px solid ${({theme:n})=>n.colors.brand.primary};
  border-radius: 50%;
  animation: spin 1s linear infinite;
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
`,Gh=document.getElementById("root");if(!Gh)throw new Error("Root element not found");ty.createRoot(Gh).render(h.jsx(Z.StrictMode,{children:h.jsx(Fw,{})}));

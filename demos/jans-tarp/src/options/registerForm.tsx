import React, { useState, KeyboardEventHandler } from 'react'
import axios from 'axios';
import './options.css'
import { v4 as uuidv4 } from 'uuid';
import { WindmillSpinner } from 'react-spinner-overlay'
import CreatableSelect from 'react-select/creatable';
import {IOption} from './IOption';

const components = {
    DropdownIndicator: null,
};

const createOption = (label: string) => ({
    label,
    value: label,
});

const RegisterForm = (data) => {
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [inputValueIssuer, setInputValueIssuer] = useState('');
    const [inputValueScope, setInputValueScope] = useState('');
    const [issuerOption, setIssuerOption] = useState<readonly IOption[]>([]);
    const [scopeOption, setScopeOption] = useState<readonly IOption[]>([createOption('openid')]);

    const handleKeyDown: KeyboardEventHandler = (event) => {
        const inputId = (event.target as HTMLInputElement).id;
        if (inputId === 'issuer') {
            if (!inputValueIssuer) return;
            switch (event.key) {
                case 'Enter':
                case 'Tab':
                    setIssuerOption([createOption(inputValueIssuer)]);
                    setInputValueIssuer('');
                    event.preventDefault();
            }
        } else if (inputId === 'scope') {
            if (!inputValueScope) return;
            switch (event.key) {
                case 'Enter':
                case 'Tab':
                    setScopeOption((prev) => [...prev, createOption(inputValueScope)]);
                    setInputValueScope('');
                    event.preventDefault();
            }

        }
    };

    function validateState() {
        let errorField = ''
        if (issuerOption.length === 0) {
            errorField += 'issuer ';
        }

        if (scopeOption.length === 0) {
            errorField += 'scope ';
        }
        if (errorField.trim() !== '') {
            setError('The following fields are mandatory: ' + errorField);
            return false;
        }
        return true;
    }

    async function registerClient() {
        if (validateState()) {
            try {
                setLoading(true);
                const response = await register()
                if (response.result !== 'success') {
                    setError('Error in registration.');
                }
                setLoading(false);
            } catch (err) {
                console.error(err)
            }
        }
    }

    async function register() {
        try {
            const issuer = issuerOption.map((iss) => iss.value)[0];
            const scope = scopeOption.map((ele) => ele.value);
            const openapiConfig = await getOpenidConfiguration(issuer);

            if (openapiConfig != undefined) {
                chrome.storage.local.set({ opConfiguration: openapiConfig.data }).then(() => {
                    console.log("openapiConfig is set to " + openapiConfig);
                });

                const registrationUrl = openapiConfig.data.registration_endpoint;

                var registerObj = {
                    issuer: issuer,
                    redirect_uris: [issuer],
                    scope: scope,
                    post_logout_redirect_uris: [chrome.runtime.getURL('options.html')],
                    response_types: ['code'],
                    grant_types: ['authorization_code', 'client_credentials'],
                    application_type: 'web',
                    client_name: 'Gluu-RP-' + uuidv4(),
                    token_endpoint_auth_method: 'client_secret_basic'
                };

                const registrationResp = await registerOIDCClient(registrationUrl, registerObj);

                if (registrationResp !== undefined) {

                    chrome.storage.local.set({
                        oidcClient: {
                            'op_host': issuer,
                            'client_id': registrationResp.data.client_id,
                            'client_secret': registrationResp.data.client_secret,
                            'scope': registerObj.scope,
                            'redirect_uri': registerObj.redirect_uris,
                            'authorization_endpoint': openapiConfig.data.authorization_endpoint,
                            'response_type': registerObj.response_types,
                            'post_logout_redirect_uris': registerObj.post_logout_redirect_uris,

                        }
                    })
                    console.log('OIDC client registered successfully!')
                    console.log("oidcClient is set for client_id: " + registrationResp.data.client_id);

                    return await { result: "success", message: "Regstration successful!" };

                } else {
                    return await { result: "error", message: "Error in registration!" };
                }
            } else {
                return await { result: "error", message: "Error in registration!" };
            }
        } catch (err) {
            console.error(err)
            return { result: "error", message: "Error in registration!" };
        }
    }

    async function registerOIDCClient(registration_endpoint, registerObj) {
        try {
            const registerReqOptions = {
                method: 'POST',
                headers: { 'content-type': 'application/json' },
                data: JSON.stringify(registerObj),
                url: registration_endpoint,
            };

            const response = await axios(registerReqOptions);
            return await response;
        } catch (err) {
            console.error(err)
        }
    }

    async function getOpenidConfiguration(issuer) {
        try {
            const endpoint = issuer + '/.well-known/openid-configuration';
            const oidcConfigOptions = {
                method: 'GET',
                url: endpoint,
            };
            const response = await axios(oidcConfigOptions);
            return await response;
        } catch (err) {
            console.error(err)
        }
    }

    return (
        <div className="box">
            <legend><span className="number">O</span> Register OIDC Client</legend>
            <legend><span className="error">{error}</span></legend>
            <WindmillSpinner loading={loading} color="#00ced1" />
            <label><b>Issuer:</b><span className="required">*</span>(Type and press enter) :</label>
            <CreatableSelect
                inputId="issuer"
                components={components}
                inputValue={inputValueIssuer}
                isClearable
                isMulti
                menuIsOpen={false}
                onChange={(newValue) => setIssuerOption(newValue)}
                onInputChange={(newValue) => setInputValueIssuer(newValue)}
                onKeyDown={handleKeyDown}
                placeholder="Type something and press enter..."
                value={issuerOption}
                className="typeahead"
            />

            <label><b>Scopes</b><span className="required">*</span> (Type and press enter) :</label>

            <CreatableSelect
                inputId='scope'
                components={components}
                inputValue={inputValueScope}
                isClearable
                isMulti
                menuIsOpen={false}
                onChange={(newValue) => setScopeOption(newValue)}
                onInputChange={(newValue) => setInputValueScope(newValue)}
                onKeyDown={handleKeyDown}
                placeholder="Type something and press enter..."
                value={scopeOption}
                className="typeahead"
            />

            <legend><span className="error">{error}</span></legend>
            <button id="sbmtButton" onClick={registerClient}>Register</button>
        </div>
    )
};

export default RegisterForm;
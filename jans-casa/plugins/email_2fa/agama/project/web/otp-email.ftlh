<#import "../kz1vc3/commons.ftlh" as com>

<@com.main>

<script src="${cache.casa_assets.contextPath}/scripts/font-awesome-5.12.1.all.min.js"></script>

<section class="pa4 shadow-4 bg-blank gray cust-section">

    <h2 class="f3 dark-blue2">${labels("casa.2fa_verification")}</h2>

    <p class="mw5 dark-red tc pv2 ph0 ma0 f6">
        <#if !matches>${labels("email2fa.wrong")}
        <#elseif expired>${labels("email2fa.expired")}
        </#if>
    </p>

    <div class="flex flex-column items-center pa3">    
        <i class="fas fa-envelope f1"></i>
        <div class="f7-cust pv2 db w5 tc">${labels("email2fa.sentCheck")}</div>
    </div>

    <form method="post" enctype="application/x-www-form-urlencoded">
        <div class="relative w5 mt1 pb2">            
            <input type="text" class="focused-text w-100 pb1 dark-gray" name="passcode"
                        pattern="[0-9]+" autocomplete="off" required autofocus>
            <#-- Pressing the enter key on this field triggers submission via the FIRST submit button found in the page -->
            <label class="focused-label-big">${labels("email2fa.enter")}</label>
        </div>
        <div class="flex justify-end mv2">
            <input class="f7-cust bw0 br1 ph4 pv2 bg-bsgreen-success white hover-bsgreen-success hover-white cust-primary-button"
                type="submit" value="${labels("casa.login")}">
        </div>
    </form>
    <form method="post" enctype="application/x-www-form-urlencoded">  
        <div class="flex flex-column items-center pt3">
            <span class="f7-cust">${labels("email2fa.noCode")}</span>
            <button type="submit" name="resend"
                class="f7-cust pa0 bw0 br0 blue hover-dark-blue underline-hover bg-transparent cust-link-button">${labels("email2fa.resend")}</button>
        </div>
    </form>

    <#if escape!false>
        <@com.alternative />
    </#if>
</section>

</@com.main>

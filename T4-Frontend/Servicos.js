// =========================================================
// 1. MICROSERVIÇO DE IA - INTERPRETADOR (Item d)
// =========================================================

// FORÇAR A FUNÇÃO NO ESCOPO GLOBAL
// IA
function escreverNoChat(mensagem, cor = "#2c3e50") {
    const chat = document.getElementById("chat-window");
    if (chat) {
        chat.innerHTML += `<p style="color: ${cor};"><strong>IA:</strong> ${mensagem}</p>`;
        chat.scrollTop = chat.scrollHeight;
    }
}

window.enviarParaIA = async function() {
    const input = document.getElementById("userInput");
    const chat = document.getElementById("chat-window");

    if (!input || !chat) return;

    const texto = input.value.trim();
    if (!texto) return;

    chat.innerHTML += `<p><strong>Você:</strong> ${texto}</p>`;
    const prompt = texto.toLowerCase();

    // Roteamento Inteligente (Item d)
    if (prompt.includes("os") || prompt.includes("ordem") || prompt.includes("serviço")) {
        escreverNoChat("Identifiquei pedido de Ordem de Serviço. Consultando dados...", "blue");
        const num = prompt.match(/\d+/);
        if(num) {
            document.getElementById("buscaOS").value = num[0];
            await consultarOS(); 
        }
    } 
    else if (prompt.includes("receita") || prompt.includes("médico") || prompt.includes("remédio")) {
        escreverNoChat("Buscando informações da Receita Médica...", "blue");
        const num = prompt.match(/\d+/);
        if(num) {
            document.getElementById("buscaRM").value = num[0];
            await consultarRM();
        }
    }
    else if (prompt.includes("cliente") || prompt.includes("paciente") || prompt.includes("médico") || prompt.includes("atendente")) {
        escreverNoChat("Localizando cadastro de pessoa...", "blue");
        const num = prompt.match(/\d+/);
        if(num) {
            // Tenta carregar no campo de código de cliente por padrão
            const campo = document.getElementById("codC") || document.getElementById("cpfP") || document.getElementById("crm");
            if(campo) {
                campo.value = num[0];
                campo.dispatchEvent(new Event('input')); // Dispara a busca JDBC automática
                setTimeout(() => {
                    const nome = document.getElementById("nomeC")?.value || document.getElementById("nomeP")?.value || document.getElementById("nomeM")?.value;
                    if(nome && nome !== "Não encontrado") escreverNoChat(`Encontrei o cadastro de: ${nome}. Dados carregados nos campos.`);
                    else escreverNoChat("Não localizei nenhuma pessoa com esse código.");
                }, 800);
            }
        }
    } else {
        escreverNoChat("Não entendi. Tente perguntar por 'OS 3', 'Receita 2' ou 'Cliente 1'.");
    }

    input.value = "";
};

//Abrir Tabs Disponíveis
function openTab(tabId, btnElement) {

    var contents = document.getElementsByClassName("tab-content");
    for (var i = 0; i < contents.length; i++) {
        contents[i].style.display = "none";
        contents[i].classList.remove("ativo");
    }
    var btns = document.getElementsByClassName("tab-btn");
    for (var i = 0; i < btns.length; i++) {
        btns[i].classList.remove("ativo");
    }
    document.getElementById(tabId).style.display = "block";
    document.getElementById(tabId).classList.add("ativo");
    btnElement.classList.add("ativo");
}
//Adiciona mais códigos de serviços
function adicionarLinha() {
    const tbody = document.querySelector("#tabelaServico");
    const row = document.createElement("tr");

    row.innerHTML = `
        <td><input class="servico-input" type="text" placeholder="Cód." style="background-color: #fcfcfc;"></td>
        <td><input class="nomeTipo-input" disabled></td>
        <td><input class="valor-input" disabled></td>
        <td>
            <i class="ri-close-circle-fill" onclick="removerLinha(this)"></i>
        </td>
    `;
    tbody.appendChild(row);
}
//Adição de Linhas da Tabela de Medicamentos
function addMedicamento() {
    const tbody = document.querySelector("#tabelaReceita");
    const row = document.createElement("tr");

    row.innerHTML = `
        <td><input class="med-class" type="text" placeholder="" style="background-color: #fcfcfc;"></td>
        <td><input class="inicio-class" type="date" style="background-color: #fcfcfc;"></td>
        <td><input class="fim-class" type="date" style="background-color: #fcfcfc;"></td>
        <td><input class="poso-class" type="text" placeholder="" style="background-color: #fcfcfc;"></td>
        <td>
            <i class="ri-close-circle-fill" onclick="removerLinha(this)"></i>
        </td>
    `;
    tbody.appendChild(row);
}
//Remoção da Linha Adicionada
function removerLinha(btn) {
    const row = btn.parentNode.parentNode;
    row.parentNode.removeChild(row);
    totalOS();
}
//Calcular Ordem de Servico
function totalOS(){
    let soma = 0;

    //Seleciona o fixo (por ID) e os dinâmicos (por classe)
    const todosOsValores = document.querySelectorAll('#valor, .valor-input');

    todosOsValores.forEach(input => {
        //Converte o valor para número. Se estiver vazio ou der erro, escreve 0.
        const valorNumerico = parseFloat(input.value.replace("R$", "").replace(",", ".")) || 0;
        soma += valorNumerico;
    });
    //Atualiza o input de total formatando para moeda brasileira
    document.getElementById('total').value = soma.toLocaleString('pt-BR', {
        style: 'currency',
        currency: 'BRL'
    });
}
//Limpar campos da Ordem de Servico
async function Limpar(){
    
    const os = ['numeroOS', 'dataEmissaoOS', 'codC', 'nomeC', 'cpfC', 'bairroC','ruaC','compC','nroC','cepC','cidC','ufC','emailsC','fonesC',
        'codA','nomeA','emailsA','fonesA','DP','servico','nomeTipo','valor','total','buscaRM','buscaOS', 'servico-input', 'nomeTipo-input', 
        'valor-input'];
    os.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
    /*
    const selects = ['selectBairro', 'selectLogradouro'];
    selects.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.selectedIndex = 0;
    });*/

    const rm = ['nroRM', 'dataRM', 'crm', 'nomeM','cpfM','bairroM','ruaM','compM','nroM','cepM','cidM','ufM','emailsM','fonesM',
        'cpfP','nomeP','bairroP','ruaP','compP','nroP','cepP','cidP','ufP','emailsP','fonesP','CID','nomeCID','med','inicio','fim','poso',
        'med-class', 'inicio-class', 'fim-class', 'poso-class'
    ];
    rm.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
}

//ADD EVENT LISTENERS

//Listener para buscar códigos dos serviços adicionados
document.querySelector("#tabelaServico").addEventListener("input", async function (e) {
    //Verifica se o input digitado é o de serviço
    if (e.target.classList.contains("servico-input")) {
        const inputServico = e.target;
        const codServico = inputServico.value.trim();
        
        //Encontra a linha (tr) onde este input está
        const linhaAtual = inputServico.closest("tr");
        const inputNome = linhaAtual.querySelector(".nomeTipo-input");
        const inputValor = linhaAtual.querySelector(".valor-input");

        if (codServico.length > 0) {
            try {
                const response = await fetch(`http://localhost:8080/api/endereco?codServico=${codServico}`);

                if (response.ok) {
                    const data = await response.json();
                    //Preenche apenas os campos da linha atual
                    inputNome.value = data.nomeTipo || "";
                    inputValor.value = data.valor || "";
                    totalOS();
                } else {
                    inputNome.value = "Não encontrado";
                    inputValor.value = "";
                }
            } catch (error) {
                console.error("Erro na busca do serviço:", error);
            }
        }
    }
});
//Buscar Cliente, Paciente, Medico e Atendente
document.getElementById("codC").addEventListener("input", async function (){
    const cod = document.getElementById('codC').value.trim();
    const pessoa = "cliente";

    try {
        const response = await fetch(`http://localhost:8080/api/endereco?cod=${cod}&pessoa=${pessoa}`);

        if (response.ok) {
            const data = await response.json();
            //console.log("Estrutura completa:", data);

            document.getElementById('nomeC').value = data.dados.nome;
            document.getElementById('cpfC').value = data.dados.cpf;
            document.getElementById('bairroC').value = data.dados.endereco.bairro.nomeBairro;
            document.getElementById('ruaC').value = data.dados.endereco.logradouro.nomeLogradouro;
            document.getElementById('compC').value = data.dados.complemento;
            document.getElementById('nroC').value = data.dados.nroMoradia;
            document.getElementById('cepC').value = data.dados.endereco.cep;
            document.getElementById('cidC').value = data.dados.endereco.cidade.nomeCidade;
            document.getElementById('ufC').value = data.dados.endereco.cidade.uf.siglaUF;

            document.getElementById('emailsC').value = data.emails 
                ? data.emails.map(e => e.enderecoEmail).join(', ') : '';
                
            document.getElementById('fonesC').value = data.fones 
                ? data.fones.map(f => f.nroTelefone).join(', ') : '';

        } else {
            console.log("Coddigo nao encontrado");
        }
    } catch (error) {
        console.error(error);
    }
});
//Busca atendente
document.getElementById("codA").addEventListener("input", async function (){
    const cod = document.getElementById('codA').value.trim();
    const pessoa = "atendente";

    try {
        const response = await fetch(`http://localhost:8080/api/endereco?cod=${cod}&pessoa=${pessoa}`);

        if (response.ok) {
            const data = await response.json();

            document.getElementById('nomeA').value = data.dados.nome;
            document.getElementById('emailsA').value = data.emails 
                ? data.emails.map(e => e.enderecoEmail).join(', ') : '';
                
            document.getElementById('fonesA').value = data.fones 
                ? data.fones.map(f => f.nroTelefone).join(', ') : '';

        } else {
            console.log("Coddigo nao encontrado");
        }
    } catch (error) {
        console.error(error);
    }
});
//Busca paciente
document.getElementById("cpfP").addEventListener("input", async function (){
    const cod = document.getElementById('cpfP').value.trim();
    const pessoa = "paciente";

    try {
        const response = await fetch(`http://localhost:8080/api/endereco?cod=${cod}&pessoa=${pessoa}`);

        if (response.ok) {
            const data = await response.json();

            document.getElementById('nomeP').value = data.dados.nome;
            document.getElementById('bairroP').value = data.dados.endereco.bairro.nomeBairro;
            document.getElementById('ruaP').value = data.dados.endereco.logradouro.nomeLogradouro;
            document.getElementById('compP').value = data.dados.complemento;
            document.getElementById('nroP').value = data.dados.nroMoradia;
            document.getElementById('cepP').value = data.dados.endereco.cep;
            document.getElementById('cidP').value = data.dados.endereco.cidade.nomeCidade;
            document.getElementById('ufP').value = data.dados.endereco.cidade.uf.siglaUF;

            document.getElementById('emailsP').value = data.emails 
                ? data.emails.map(e => e.enderecoEmail).join(', ') : '';
                
            document.getElementById('fonesP').value = data.fones 
                ? data.fones.map(f => f.nroTelefone).join(', ') : '';

        } else {
            console.log("Coddigo nao encontrado");
        }
    } catch (error) {
        console.error(error);
    }
});
//Busca do crm
document.getElementById("crm").addEventListener("input", async function (){
    const cod = document.getElementById('crm').value.trim();
    const pessoa = "medico";

    try {
        const response = await fetch(`http://localhost:8080/api/endereco?cod=${cod}&pessoa=${pessoa}`);

        if (response.ok) {
            const data = await response.json();

            document.getElementById('nomeM').value = data.dados.nome;
            document.getElementById('cpfM').value = data.dados.cpf;
            document.getElementById('bairroM').value = data.dados.endereco.bairro.nomeBairro;
            document.getElementById('ruaM').value = data.dados.endereco.logradouro.nomeLogradouro;
            document.getElementById('compM').value = data.dados.complemento;
            document.getElementById('nroM').value = data.dados.nroMoradia;
            document.getElementById('cepM').value = data.dados.endereco.cep;
            document.getElementById('cidM').value = data.dados.endereco.cidade.nomeCidade;
            document.getElementById('ufM').value = data.dados.endereco.cidade.uf.siglaUF;

            document.getElementById('emailsM').value = data.emails 
                ? data.emails.map(e => e.enderecoEmail).join(', ') : '';
                
            document.getElementById('fonesM').value = data.fones 
                ? data.fones.map(f => f.nroTelefone).join(', ') : '';

        } else {
            console.log("Coddigo nao encontrado");
        }
    } catch (error) {
        console.error(error);
    }
});
//Buscar Servico da instância fixa
document.getElementById("tabelaServico").addEventListener("input", async function (){
    const codServico = document.getElementById('servico').value.trim();

    try{
        const response = await fetch(`http://localhost:8080/api/endereco?codServico=${codServico}`);

        if (response.ok) {
            const data = await response.json();

            document.getElementById('nomeTipo').value = data.nomeTipo;
            document.getElementById('valor').value = data.valor;
            totalOS();

        } else {
            console.log("Coddigo nao encontrado");
        }
    } catch (error) {
        console.error(error);
    }
});
//Buscar codigo CID
document.getElementById("CID").addEventListener("input", async function (){
    const cod = document.getElementById('CID').value.trim();

    try {
        const response = await fetch(`http://localhost:8080/api/endereco?CID=${cod}`);

        if (response.ok) {
            const data = await response.json();
            document.getElementById('nomeCID').value = data.nome;

        } else {
            console.log("Código CID inválido!");
        }
    } catch (error) {
        console.error(error);
    }
});

//FUNÇÕES PRINCIPAIS

//Ordem de Servico
async function cadastrarOS(){
    const nrOS = parseInt(document.getElementById('numeroOS').value);
    const dataOS = document.getElementById('dataEmissaoOS').value.trim();
    const codCliente = parseInt(document.getElementById('codC').value);
    const codAtendente = parseInt(document.getElementById('codA').value);
    const descricao = document.getElementById('DP').value.trim();
    const total = document.getElementById('total').value.trim();

    // Remove "R$", espaços e pontos, e troca a vírgula por ponto
    const totalOS = parseFloat(total.replace(/[R$\s.]/g, '').replace(',', '.')) || 0;
    const codServico = document.getElementById('servico').value; 
    const classeCodServico = document.querySelectorAll('.servico-input');
    const codServicoJS = [];

    classeCodServico.forEach(input => {
        const valor = input.value.trim();
        if (valor !== "") {
            codServicoJS.push(valor); 
        }
    });

    //Validações dos Dados
    if (!descricao) {
        alert('Necessário preencher a descrição do problema.', 'error');
    }
    if (isNaN(nrOS) || nrOS <= 0) {
        alert('Necessário preencher o número da ordem de serviço.', 'error');
    }
    if (isNaN(codCliente) || codCliente <= 0) {
        alert('Necessário preencher o código do cliente.', 'error');
    }
    if (isNaN(codAtendente) || codAtendente <= 0) {
        alert('Necessário preencher o código do atendente.', 'error');
    }

    const cadastro2 = {
        acao: "ordemServico",
        nrOS: nrOS,
        dataOS: dataOS,
        codCliente: codCliente,
        codAtendente: codAtendente,
        descricao: descricao,
        totalOS: totalOS,
        codServico: codServico,
        codServicoJS: codServicoJS
    };
    console.log(cadastro2)

    try{
        const response = await fetch("http://localhost:8080/api/endereco",{
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(cadastro2)
        });

        const data = await response.json();

        if (response.ok) {
            alert("Cadastro de Ordem de Serviço realizado com sucesso.");
        } else {
            console.error('Erro da API:', data);
            //alert(data.message);
            alert("Erro ao cadastrar!");
        }
    }catch (error) {
        console.error('Erro na submissão:', error);
    }
    Limpar();
}
//Consulta
async function consultarOS(){
    const valor = document.getElementById('buscaOS').value.trim();
    const messageDiv = document.getElementById('resultadoBusca');

    try {
        const response = await fetch(`http://localhost:8080/api/endereco?buscaOS=${valor}`);
        
        if (!response.ok) {
            const erroTxt = await response.text();
            console.error("Erro do servidor:", erroTxt);
            return;
        }
        const data = await response.json(); 
        console.log("Dados recebidos:", data);

        const resumoHTML = ` 
            <div class="destaque">
                <span class="nome_destaque searches">📝 Ordem de Serviço Nº:${data.nroOS}</span>
                <p><strong>Data ........................</strong> ${data.data}</p>
                <p><strong>Cliente .....................</strong> ${data.clienteNome}</p>
                <p><strong>Total .........................</strong> R$ ${data.total}</p>
                <strong>Serviços ...................</strong> ${data.servicos.join(', ')}
            </div>
        `;
        messageDiv.innerHTML = resumoHTML;
        messageDiv.style.display = "block"; 
        document.getElementById("resultadoBusca")?.scrollIntoView({ behavior: "smooth" });
        Limpar();

    } catch (error) {
        console.error("Erro na leitura do JSON:", error);
        alert("O servidor enviou um dado inválido. Verifique o console do Java.");
    }
}
//Cadastro
async function cadastrarRM() {
    //Captura campos fixos
    const nrRM = parseInt(document.getElementById('nroRM').value);
    const dataRM = document.getElementById('dataRM').value;
    const crm = document.getElementById('crm').value.trim();
    const cpfPaciente = document.getElementById('cpfP').value.trim();
    const codCID = document.getElementById('CID').value.trim();

    const listaPrescricoes = [];

    //Captura a primeira linha (fixa)
    const medPrincipal = document.getElementById('med').value.trim();
    if (medPrincipal) {
        listaPrescricoes.push({
            medicamento: medPrincipal,
            dataInicio: document.getElementById('inicio').value,
            dataFim: document.getElementById('fim').value,
            posologia: document.getElementById('poso').value.trim()
        });
    }

    //Captura linhas extras da tabela
    const linhasExtras = document.querySelectorAll("#tabelaReceita tr");
    linhasExtras.forEach(linha => {
        const med = linha.querySelector(".med-class")?.value.trim();
        if (med) {
            listaPrescricoes.push({
                medicamento: med,
                dataInicio: linha.querySelector(".inicio-class").value,
                dataFim: linha.querySelector(".fim-class").value,
                posologia: linha.querySelector(".poso-class").value.trim()
            });
        }
    });

    //Validações dos Dados
    if (!codCID) {
        alert('Necessário preencher o código CID.', 'error');
    }
    if (!cpfPaciente) {
        alert('Necessário preencher o CPF do paciente.', 'error');
    }
    if (!crm) {
        alert('Necessário preencher o CRM do médico.', 'error');
    }
    if (isNaN(nrRM) || nrRM <= 0) {
        alert('Necessário preencher o número da receita médica.', 'error');
    }

    //Monta o objeto EXATAMENTE como o Servlet espera
    const dadosEnvio = {
        acao: "receitaMedica", 
        nrRM: nrRM,
        dataRM: dataRM,
        crm: crm,
        cpfPaciente: cpfPaciente,
        codCID: codCID,
        itens: listaPrescricoes
    };

    console.log("Enviando:", dadosEnvio);

    try {
        const response = await fetch("http://localhost:8080/api/endereco", {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(dadosEnvio)
        });

        const data = await response.json();
        if (response.ok) {
            alert("Receita Médica cadastrada com sucesso!");
        } else {
            //alert("Erro: " + data.message);
            alert("Erro ao cadastrar!");
        }
    } catch (error) {
        console.error('Erro na submissão:', error);
    }
    Limpar();
}
//Consultar Receita Medica
async function consultarRM() {
    const valor = document.getElementById('buscaRM').value.trim();
    const messageDiv = document.getElementById('resultadoBusca');

    if (!valor) return alert("Informe o número da Receita.");

    try {
        const response = await fetch(`http://localhost:8080/api/endereco?buscaRM=${valor}`);
        
        if (response.ok) {
            const data = await response.json();
            console.log(data);
            
            //Tratando a lista de itens (medicamentos)
            let listaHTML = "";
            console.log(data.itens);
            if (data.itens && data.itens.length > 0) {
                listaHTML = data.itens.map(item => `
                    <div>
                        <p><strong>Medicamento:</strong> ${item.medicamento} </p>
                        <p><strong>Posologia:</strong> ${item.posologia} </p>
                        <p><strong>Duração:</strong> ${item.inicio} até ${item.fim}</p>
                    </div>
                `).join('');
            } else {
                listaHTML = "<li>Nenhum medicamento encontrado para esta receita.</li>";
            }

            //Montando o HTML com as chaves exatas do JSON
            const resumoHTML = ` 
                <div class="destaque" style="margin-bottom: 2rem;">
                    <span class="nome_destaque searches">🏥 Receita Médica Nº:${data.receita.nroReceita}</span>
                    <p><strong>Data ......................</strong> ${data.receita.dataEmissao}</p>
                    <p><strong>Paciente ................</strong> ${data.receita.paciente}</p>
                    <p><strong>Médico ..................</strong> ${data.receita.medico}</p>
                    <p><strong>CID .........................</strong> ${data.receita.cidNome}</p>
                </div>
                <div class="destaque">
                    <span class="nome_destaque searches">💊 Prescrições</span>
                    <ul style="list-style: none; padding: 0; margin-top: 10px;">
                        ${listaHTML}
                    </ul>
                </div>
            `;
            
            messageDiv.innerHTML = resumoHTML;
            messageDiv.style.display = "block"; 
            document.getElementById("resultadoBusca")?.scrollIntoView({ behavior: "smooth" });
            Limpar();

        } else {
            messageDiv.innerHTML = "Receita não encontrada.";
            messageDiv.style.display = "block";
        }
    } catch (error) {
        console.error("Erro na consulta:", error);
    }
}
        